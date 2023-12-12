package com.chwipoClova.resume.service;

import com.chwipoClova.common.exception.CommonException;
import com.chwipoClova.common.exception.ExceptionCode;
import com.chwipoClova.common.response.CommonResponse;
import com.chwipoClova.common.response.MessageCode;
import com.chwipoClova.resume.entity.Resume;
import com.chwipoClova.resume.repository.ResumeRepository;
import com.chwipoClova.resume.request.ResumeDeleteOldReq;
import com.chwipoClova.resume.request.ResumeDeleteReq;
import com.chwipoClova.resume.response.ResumeListRes;
import com.chwipoClova.resume.response.ResumeUploadRes;
import com.chwipoClova.user.entity.User;
import com.chwipoClova.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ResumeService {

    @Value("${file.upload.resume.path}")
    private String uploadPath;

    @Value("${file.upload.resume.max-size}")
    private Long uploadMaxSize;

    @Value("${file.upload.resume.type}")
    private String uploadType;

    @Value("${limit.size.resume}")
    private Integer resumeLimitSize;

    private final ResumeRepository resumeRepository;

    private final UserRepository userRepository;

    @Transactional
    public ResumeUploadRes uploadResume(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));

        String contentType = file.getContentType();
        assert contentType != null;

        if (contentType.toLowerCase().indexOf(uploadType) == -1) {
            throw new CommonException(ExceptionCode.FILE_EXT_PDF.getMessage(), ExceptionCode.FILE_EXT_PDF.getCode());
        }

        String originalName = file.getOriginalFilename();
        assert originalName != null;

        // 기존 이력서 목록이 3건 이상이면 오류 발생
        List<Resume> resumeList = resumeRepository.findByUserUserIdOrderByRegDate(user.getUserId());
        if (resumeList != null && resumeList.size() >= resumeLimitSize) {
            throw new CommonException(ExceptionCode.RESUME_LIST_OVER.getMessage(), ExceptionCode.RESUME_LIST_OVER.getCode());
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        // 날짜 폴더 생성
        String folderPath = makeFolder();

        // UUID
        String uuid = UUID.randomUUID().toString();

        long currentTimeMills = Timestamp.valueOf(LocalDateTime.now()).getTime();

        String filePath = uploadPath + File.separator + folderPath + File.separator;
        String fileName = uuid + "_" + currentTimeMills + "." +extension;
        Long fileSize = file.getSize();

        if (fileSize > uploadMaxSize) {
            new CommonException(ExceptionCode.FILE_SIZE.getMessage(), ExceptionCode.FILE_SIZE.getCode());
        }

        // 저장할 파일 이름 중간에 "_"를 이용해서 구현
        String saveName = filePath + fileName;
        Path savePath = Paths.get(saveName);
        file.transferTo(savePath);

        // TODO 업로드 성공 후 요약 저장
        String summary = originalName + "요약";

        // TODO 등록 전 개수 제한 필요
        
        // 파일업로드 성공 후 DB 저장
        Resume resume = Resume.builder()
                .fileName(fileName)
                .filePath(filePath)
                .fileSize(fileSize)
                .originalFileName(originalName)
                .summary(summary)
                .user(user)
                .build();

        Resume resumeRst = resumeRepository.save(resume);
        return ResumeUploadRes.builder().userId(userId).resumeId(resumeRst.getResumeId()).build();
    }

    /*날짜 폴더 생성*/
    private String makeFolder() {

        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String folderPath = str.replace("/", File.separator);

        // make folder --------
        File uploadPathFolder = new File(uploadPath, folderPath);

        if(!uploadPathFolder.exists()) {
            boolean mkdirs = uploadPathFolder.mkdirs();
            log.info("-------------------makeFolder------------------");
            log.info("uploadPathFolder.exists() : {}", uploadPathFolder.exists());
            log.info("mkdirs : {}", mkdirs);
        }
        return folderPath;

    }

    public List<ResumeListRes> selectResumeList(Long userId) {

        List<ResumeListRes> resumeListResList = new ArrayList<>();

        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));

        List<Resume> resumeList = resumeRepository.findByUserUserIdOrderByRegDate(user.getUserId());

        resumeList.stream().forEach(resume -> {
            ResumeListRes resumeListRes = ResumeListRes.builder()
                    .resumeId(resume.getResumeId())
                    .fileName(resume.getOriginalFileName())
                    .regDate(resume.getRegDate())
                    .build();
            resumeListResList.add(resumeListRes);
        });

        return resumeListResList;
    }


    @Transactional
    public CommonResponse deleteResume(ResumeDeleteReq resumeDeleteReq) {
        Long resumeId = resumeDeleteReq.getResumeId();
        Long userId = resumeDeleteReq.getUserId();

        userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));
        Resume resume = resumeRepository.findByUserUserIdAndResumeId(userId, resumeId).orElseThrow(() -> new CommonException(ExceptionCode.RESUME_NULL.getMessage(), ExceptionCode.RESUME_NULL.getCode()));
        return resumeDelete(resume);
    }


    @Transactional
    public CommonResponse deleteOldResume(ResumeDeleteOldReq resumeDeleteOldReq) {
        Long userId = resumeDeleteOldReq.getUserId();

        userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));
        Resume resume = resumeRepository.findTop1ByUserUserIdOrderByRegDate(userId).orElseThrow(() -> new CommonException(ExceptionCode.RESUME_NULL.getMessage(), ExceptionCode.RESUME_NULL.getCode()));
        return resumeDelete(resume);
    }

    private CommonResponse resumeDelete(Resume resume) {
        String filePath = resume.getFilePath();
        String fileName = resume.getFileName();

        File file = new File(filePath + fileName);

        if (!file.exists()) {
            log.error("파일이 존재 하지 않습니다. {}", filePath + fileName);
        } else {
            if (!file.delete()) {
                log.error("파일 삭제 실패했습니다. {}", filePath + fileName);
            }
        }

        resumeRepository.delete(resume);
        return new CommonResponse<>(MessageCode.OK.getCode(), null, MessageCode.OK.getMessage());
    }
}
