package com.xxx.animal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.animal.common.Result;
import com.xxx.animal.entity.DonationClaim;
import com.xxx.animal.service.DonationClaimService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class DonationClaimController {
    private final DonationClaimService donationClaimService;

    public DonationClaimController(DonationClaimService donationClaimService) {
        this.donationClaimService = donationClaimService;
    }

    @PostMapping("/api/donation/claims")
    public Result<Boolean> create(@RequestBody DonationClaim payload) {
        if (payload.getStatus() == null) payload.setStatus(0);
        return Result.ok(donationClaimService.save(payload));
    }

    @GetMapping("/api/admin/donation-claims")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize
    ) {
        LambdaQueryWrapper<DonationClaim> wrapper = new LambdaQueryWrapper<>();
        if (status != null && status >= 0) wrapper.eq(DonationClaim::getStatus, status);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(DonationClaim::getNeedName, keyword)
                    .or()
                    .like(DonationClaim::getContactName, keyword)
                    .or()
                    .like(DonationClaim::getPhone, keyword));
        }
        wrapper.orderByDesc(DonationClaim::getCreateTime).orderByDesc(DonationClaim::getId);
        Page<DonationClaim> pageResult = donationClaimService.page(new Page<>(page, pageSize), wrapper);
        return Result.ok(Map.of(
                "records", pageResult.getRecords(),
                "total", pageResult.getTotal()
        ));
    }

    @PutMapping("/api/admin/donation-claims/{id}/status")
    public Result<Boolean> review(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        DonationClaim row = new DonationClaim();
        row.setId(id);
        Object statusObj = payload.get("status");
        int status = statusObj instanceof Number ? ((Number) statusObj).intValue() : Integer.parseInt(String.valueOf(statusObj));
        row.setStatus(status);
        Object note = payload.get("reviewNote");
        row.setReviewNote(note == null ? null : String.valueOf(note));
        return Result.ok(donationClaimService.updateById(row));
    }

    @DeleteMapping("/api/admin/donation-claims/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(donationClaimService.removeById(id));
    }
}
