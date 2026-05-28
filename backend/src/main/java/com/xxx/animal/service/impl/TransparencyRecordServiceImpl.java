package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.TransparencyRecord;
import com.xxx.animal.mapper.TransparencyRecordMapper;
import com.xxx.animal.service.TransparencyRecordService;
import org.springframework.stereotype.Service;

@Service
public class TransparencyRecordServiceImpl extends ServiceImpl<TransparencyRecordMapper, TransparencyRecord> implements TransparencyRecordService {
}
