package com.chwipoClova.recruit.service;


import com.chwipoClova.common.exception.CommonException;
import com.chwipoClova.common.exception.ExceptionCode;
import com.chwipoClova.recruit.entity.Recruit;
import com.chwipoClova.recruit.repository.RecruitRepository;
import com.chwipoClova.recruit.request.RecruitInsertReq;
import com.chwipoClova.recruit.response.RecruitInsertRes;
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
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecruitService {

    @Value("${file.upload.recruit.path}")
    private String uploadPath;

    @Value("${file.upload.recruit.max-size}")
    private Long uploadMaxSize;

    @Value("${file.upload.recruit.type}")
    private String uploadType;

    private final UserRepository userRepository;

    private final RecruitRepository recruitRepository;

    @Transactional
    public RecruitInsertRes insertRecruit(RecruitInsertReq recruitInsertReq) throws IOException {
        Long userId = recruitInsertReq.getUserId();
        String recruitContent = recruitInsertReq.getRecruitContent();
        MultipartFile file = recruitInsertReq.getFile();

        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode()));

        if (org.apache.commons.lang3.StringUtils.isBlank(recruitContent) && file == null) {
            throw new CommonException(ExceptionCode.RECRUIT_CONTENT_NULL.getMessage(), ExceptionCode.RECRUIT_CONTENT_NULL.getCode());
        }

        Recruit recruit;

        if (org.apache.commons.lang3.StringUtils.isNotBlank(recruitContent)) {
            // TODO 채용 공고 텍스트 요약
            String summary = recruitContent + "요약";

            // TODO 채용 공고 제목 추출
            String title = recruitContent + "제목";

            recruit = Recruit.builder()
                    .title(title)
                    .content(recruitContent)
                    .summary(summary)
                    .user(user)
                    .build();
        } else {
            String contentType = file.getContentType();
            assert contentType != null;

            if (contentType.toLowerCase().indexOf(uploadType) == -1) {
                throw new CommonException(ExceptionCode.FILE_EXT_IMAGE.getMessage(), ExceptionCode.FILE_EXT_IMAGE.getCode());
            }

            String originalName = file.getOriginalFilename();
            assert originalName != null;

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

            // TODO 채용 공고 이미지 요약
            String summary = originalName + "요약";

            // TODO 채용 공고 제목 추출
            String title = originalName + "제목";

            recruit = Recruit.builder()
                    .title(title)
                    .fileName(fileName)
                    .filePath(filePath)
                    .fileSize(fileSize)
                    .originalFileName(originalName)
                    .user(user)
                    .summary(summary)
                    .build();
        }

        Recruit recruitRst = recruitRepository.save(recruit);

        RecruitInsertRes recruitInsertRes = RecruitInsertRes.builder()
                .recruitId(recruitRst.getRecruitId())
                .title(recruitRst.getTitle())
                .summary(recruitRst.getSummary())
                .build();

        return recruitInsertRes;
    }
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

    @Transactional
    public void deleteBeforeRecruit() {
        Timestamp baseDate = Timestamp.valueOf(LocalDate.now().minusDays(14).atStartOfDay());
        List<Recruit> recruitList = recruitRepository.findByRegDateLessThanEqual(baseDate);
        if (recruitList != null) {
            if (recruitList.size() > 0) {
                log.info("deleteBeforeRecruit size {}, baseDate {}", recruitList.size() , baseDate);
                recruitRepository.deleteAll(recruitList);
            }
        }
    }
}
