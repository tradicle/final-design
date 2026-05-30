package com.xxx.animal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.animal.entity.AdoptionApplication;

public interface AdoptionApplicationService extends IService<AdoptionApplication> {
    Page<AdoptionApplication> listWithPetName(Page<AdoptionApplication> page, LambdaQueryWrapper<AdoptionApplication> wrapper);
}
