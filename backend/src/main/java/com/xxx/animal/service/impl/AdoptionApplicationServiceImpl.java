package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.AdoptionApplication;
import com.xxx.animal.mapper.AdoptionApplicationMapper;
import com.xxx.animal.service.AdoptionApplicationService;
import org.springframework.stereotype.Service;

@Service
public class AdoptionApplicationServiceImpl extends ServiceImpl<AdoptionApplicationMapper, AdoptionApplication> implements AdoptionApplicationService {
}
