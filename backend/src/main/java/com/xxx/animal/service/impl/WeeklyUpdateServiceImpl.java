package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.WeeklyUpdate;
import com.xxx.animal.mapper.WeeklyUpdateMapper;
import com.xxx.animal.service.WeeklyUpdateService;
import org.springframework.stereotype.Service;

@Service
public class WeeklyUpdateServiceImpl extends ServiceImpl<WeeklyUpdateMapper, WeeklyUpdate> implements WeeklyUpdateService {
}
