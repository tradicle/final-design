package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.DonationRecord;
import com.xxx.animal.mapper.DonationRecordMapper;
import com.xxx.animal.service.DonationRecordService;
import org.springframework.stereotype.Service;

@Service
public class DonationRecordServiceImpl extends ServiceImpl<DonationRecordMapper, DonationRecord> implements DonationRecordService {
}
