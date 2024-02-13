package com.kos.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.kos.backend.pojo.Record;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {
}
