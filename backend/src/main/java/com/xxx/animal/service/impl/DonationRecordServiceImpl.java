package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.DonationRecord;
import com.xxx.animal.mapper.DonationRecordMapper;
import com.xxx.animal.service.DonationRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonationRecordServiceImpl extends ServiceImpl<DonationRecordMapper, DonationRecord> implements DonationRecordService {

    @Override
    public void shiftSortOrder() {
        UpdateWrapper<DonationRecord> wrapper = new UpdateWrapper<>();
        wrapper.setSql("sort_order = COALESCE(sort_order, 0) + 1");
        update(wrapper);
    }

    @Override
    @Transactional
    public boolean createWithSortShift(DonationRecord entity) {
        shiftSortOrder();
        entity.setSortOrder(1);
        return save(entity);
    }
}
