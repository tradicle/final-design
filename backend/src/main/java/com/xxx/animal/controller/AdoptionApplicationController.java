package com.xxx.animal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.animal.common.Result;
import com.xxx.animal.entity.AdoptionApplication;
import com.xxx.animal.entity.User;
import com.xxx.animal.service.AdoptionApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AdoptionApplicationController {
    private final AdoptionApplicationService adoptionApplicationService;

    public AdoptionApplicationController(AdoptionApplicationService adoptionApplicationService) {
        this.adoptionApplicationService = adoptionApplicationService;
    }

    @PostMapping("/api/adoption-applications")
    public Result<Boolean> create(@RequestBody AdoptionApplication payload, HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser != null) {
            payload.setUserId(loginUser.getId());
        }
        if (payload.getStatus() == null) payload.setStatus(0);
        return Result.ok(adoptionApplicationService.save(payload));
    }

    @GetMapping("/api/admin/adoption-applications")
    public Result<Map<String, Object>> listForAdmin(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize
    ) {
        LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<>();
        if (status != null && status >= 0) {
            wrapper.eq(AdoptionApplication::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(AdoptionApplication::getApplicantName, keyword)
                    .or()
                    .like(AdoptionApplication::getPhone, keyword)
                    .or()
                    .like(AdoptionApplication::getAddress, keyword));
        }
        wrapper.orderByDesc(AdoptionApplication::getCreateTime).orderByDesc(AdoptionApplication::getId);
        Page<AdoptionApplication> pageResult = adoptionApplicationService.page(new Page<>(page, pageSize), wrapper);
        return Result.ok(Map.of(
                "records", pageResult.getRecords(),
                "total", pageResult.getTotal()
        ));
    }

    @PutMapping("/api/admin/adoption-applications/{id}/status")
    public Result<Boolean> review(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        AdoptionApplication row = new AdoptionApplication();
        row.setId(id);
        Object statusObj = payload.get("status");
        int status = statusObj instanceof Number ? ((Number) statusObj).intValue() : Integer.parseInt(String.valueOf(statusObj));
        row.setStatus(status);
        Object note = payload.get("reviewNote");
        row.setReviewNote(note == null ? null : String.valueOf(note));
        return Result.ok(adoptionApplicationService.updateById(row));
    }

    @DeleteMapping("/api/admin/adoption-applications/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(adoptionApplicationService.removeById(id));
    }
}
