package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.UrgentNeed;
import com.xxx.animal.mapper.UrgentNeedMapper;
import com.xxx.animal.service.UrgentNeedService;
import org.springframework.stereotype.Service;

@Service
public class UrgentNeedServiceImpl extends ServiceImpl<UrgentNeedMapper, UrgentNeed> implements UrgentNeedService {
}
