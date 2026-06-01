package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.WeeklyUpdate;
import com.xxx.animal.mapper.WeeklyUpdateMapper;
import com.xxx.animal.service.WeeklyUpdateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WeeklyUpdateServiceImpl extends ServiceImpl<WeeklyUpdateMapper, WeeklyUpdate> implements WeeklyUpdateService {

    @Override
    public void shiftSortOrder() {
        UpdateWrapper<WeeklyUpdate> wrapper = new UpdateWrapper<>();
        wrapper.setSql("sort_order = COALESCE(sort_order, 0) + 1");
        update(wrapper);
    }

    @Override
    @Transactional
    public boolean createWithSortShift(WeeklyUpdate entity) {
        shiftSortOrder();
        entity.setSortOrder(1);
        return save(entity);
    }
}
