package com.blockchain.larisa.mapper;

import com.blockchain.larisa.domain.KLine;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KLineMapper {

    /**
     * 插入一条新的K线
     * @param kLine
     * @return 成功行数
     */
    int insert(KLine kLine);
}
