package com.chwipoClova.tmp.service;

import com.chwipoClova.tmp.entity.Tmp;
import com.chwipoClova.tmp.repository.TmpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TmpService {

    private final TmpRepository tmpRepository;

    public List<Tmp> selectTmpList() {
        return tmpRepository.findAll();
    }
}
