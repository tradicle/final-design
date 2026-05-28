package com.xxx.animal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.animal.common.Result;
import com.xxx.animal.entity.DonationRecord;
import com.xxx.animal.entity.UrgentNeed;
import com.xxx.animal.service.DonationRecordService;
import com.xxx.animal.service.UrgentNeedService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donation")
public class DonationController {

    private final DonationRecordService donationRecordService;
    private final UrgentNeedService urgentNeedService;

    public DonationController(DonationRecordService donationRecordService, UrgentNeedService urgentNeedService) {
        this.donationRecordService = donationRecordService;
        this.urgentNeedService = urgentNeedService;
    }

    @GetMapping("/records")
    public Result<List<DonationRecord>> listRecords() {
        LambdaQueryWrapper<DonationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DonationRecord::getSortOrder).orderByDesc(DonationRecord::getId);
        return Result.ok(donationRecordService.list(wrapper));
    }

    @GetMapping("/urgent")
    public Result<List<UrgentNeed>> listUrgent() {
        LambdaQueryWrapper<UrgentNeed> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(UrgentNeed::getSortOrder).orderByDesc(UrgentNeed::getId);
        return Result.ok(urgentNeedService.list(wrapper));
    }
}
