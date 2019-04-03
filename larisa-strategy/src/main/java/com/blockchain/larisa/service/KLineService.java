package com.blockchain.larisa.service;

import com.blockchain.larisa.mapper.KLineMapper;
import com.blockchain.larisa.domain.KLine;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class KLineService {

    @Resource
    private KLineMapper kLineMapper;

    public boolean insertKline(KLine kLine) {
        int cnt = kLineMapper.insert(kLine);
        return cnt == 1;
    }
}
