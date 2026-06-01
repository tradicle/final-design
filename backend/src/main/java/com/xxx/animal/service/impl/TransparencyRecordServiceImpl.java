package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.TransparencyRecord;
import com.xxx.animal.mapper.TransparencyRecordMapper;
import com.xxx.animal.service.TransparencyRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransparencyRecordServiceImpl extends ServiceImpl<TransparencyRecordMapper, TransparencyRecord> implements TransparencyRecordService {

    @Override
    public void shiftSortOrder() {
        UpdateWrapper<TransparencyRecord> wrapper = new UpdateWrapper<>();
        wrapper.setSql("sort_order = COALESCE(sort_order, 0) + 1");
        update(wrapper);
    }

    @Override
    @Transactional
    public boolean createWithSortShift(TransparencyRecord entity) {
        shiftSortOrder();
        entity.setSortOrder(1);
        return save(entity);
    }
}
