package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.UrgentNeed;
import com.xxx.animal.mapper.UrgentNeedMapper;
import com.xxx.animal.service.UrgentNeedService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UrgentNeedServiceImpl extends ServiceImpl<UrgentNeedMapper, UrgentNeed> implements UrgentNeedService {

    @Override
    public void shiftSortOrder() {
        UpdateWrapper<UrgentNeed> wrapper = new UpdateWrapper<>();
        wrapper.setSql("sort_order = COALESCE(sort_order, 0) + 1");
        update(wrapper);
    }

    @Override
    @Transactional
    public boolean createWithSortShift(UrgentNeed entity) {
        shiftSortOrder();
        entity.setSortOrder(1);
        return save(entity);
    }

    @Override
    public void renumberSortOrder() {
        LambdaQueryWrapper<UrgentNeed> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(UrgentNeed::getSortOrder).orderByDesc(UrgentNeed::getId);
        List<UrgentNeed> rows = list(wrapper);
        if (rows.isEmpty()) {
            return;
        }
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setSortOrder(i + 1);
        }
        updateBatchById(rows);
    }

    @Override
    @Transactional
    public boolean deleteWithRenumber(Long id) {
        boolean result = removeById(id);
        renumberSortOrder();
        return result;
    }
}
