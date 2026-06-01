package com.xxx.animal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.animal.entity.DonationRecord;

public interface DonationRecordService extends IService<DonationRecord> {

    /**
     * 将所有现有记录的 sort_order 加 1（顺位下移），为新记录置顶腾出位置。
     * 使用单条 SQL UPDATE 确保原子性，避免 list-then-update 的竞态条件。
     */
    void shiftSortOrder();

    /**
     * 先执行排序移位，再将新记录的 sortOrder 设为 1 后保存，确保新记录排在最前面。
     */
    boolean createWithSortShift(DonationRecord entity);
}
