package com.xxx.animal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.animal.entity.UrgentNeed;

public interface UrgentNeedService extends IService<UrgentNeed> {

    /**
     * 将所有现有记录的 sort_order 加 1（顺位下移），为新记录置顶腾出位置。
     * 使用单条 SQL UPDATE 确保原子性。
     */
    void shiftSortOrder();

    /**
     * 先执行排序移位，再将新记录的 sortOrder 设为 1 后保存，确保新记录排在最前面。
     */
    boolean createWithSortShift(UrgentNeed entity);

    /**
     * 将剩余记录按 sortOrder 升序重新编号为 1, 2, 3...，消除因删除造成的序号空缺。
     */
    void renumberSortOrder();

    /**
     * 删除指定记录后自动重排剩余记录的 sortOrder，保证序号连续。
     * 包含事务保证原子性。
     */
    boolean deleteWithRenumber(Long id);
}
