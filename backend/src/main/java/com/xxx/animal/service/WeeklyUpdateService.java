package com.xxx.animal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.animal.entity.WeeklyUpdate;

public interface WeeklyUpdateService extends IService<WeeklyUpdate> {

    /**
     * 将所有现有记录的 sort_order 加 1（顺位下移），为新记录置顶腾出位置。
     */
    void shiftSortOrder();

    /**
     * 先执行排序移位，再将新记录的 sortOrder 设为 1 后保存，确保新记录排在最前面。
     */
    boolean createWithSortShift(WeeklyUpdate entity);
}
