package com.xxx.animal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.animal.common.Result;
import com.xxx.animal.entity.DonationRecord;
import com.xxx.animal.entity.SystemConfig;
import com.xxx.animal.entity.TransparencyRecord;
import com.xxx.animal.entity.UrgentNeed;
import com.xxx.animal.entity.WeeklyUpdate;
import com.xxx.animal.service.DonationRecordService;
import com.xxx.animal.service.SystemConfigService;
import com.xxx.animal.service.TransparencyRecordService;
import com.xxx.animal.service.UrgentNeedService;
import com.xxx.animal.service.WeeklyUpdateService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/content")
public class AdminContentController {
    private final WeeklyUpdateService weeklyUpdateService;
    private final TransparencyRecordService transparencyRecordService;
    private final UrgentNeedService urgentNeedService;
    private final DonationRecordService donationRecordService;
    private final SystemConfigService systemConfigService;

    public AdminContentController(
            WeeklyUpdateService weeklyUpdateService,
            TransparencyRecordService transparencyRecordService,
            UrgentNeedService urgentNeedService,
            DonationRecordService donationRecordService,
            SystemConfigService systemConfigService
    ) {
        this.weeklyUpdateService = weeklyUpdateService;
        this.transparencyRecordService = transparencyRecordService;
        this.urgentNeedService = urgentNeedService;
        this.donationRecordService = donationRecordService;
        this.systemConfigService = systemConfigService;
    }

    @GetMapping("/weekly-updates")
    public Result<List<WeeklyUpdate>> listWeeklyUpdates() {
        LambdaQueryWrapper<WeeklyUpdate> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(WeeklyUpdate::getSortOrder).orderByDesc(WeeklyUpdate::getId);
        return Result.ok(weeklyUpdateService.list(wrapper));
    }

    @PostMapping("/weekly-updates")
    @Transactional
    public Result<Boolean> createWeeklyUpdate(@RequestBody WeeklyUpdate payload) {
        shiftWeeklyUpdatesSortOrder();
        payload.setSortOrder(1);
        return Result.ok(weeklyUpdateService.save(payload));
    }

    @PutMapping("/weekly-updates/{id}")
    public Result<Boolean> updateWeeklyUpdate(@PathVariable Long id, @RequestBody WeeklyUpdate payload) {
        payload.setId(id);
        return Result.ok(weeklyUpdateService.updateById(payload));
    }

    @DeleteMapping("/weekly-updates/{id}")
    public Result<Boolean> deleteWeeklyUpdate(@PathVariable Long id) {
        return Result.ok(weeklyUpdateService.removeById(id));
    }

    @GetMapping("/transparency")
    public Result<List<TransparencyRecord>> listTransparency() {
        LambdaQueryWrapper<TransparencyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(TransparencyRecord::getSortOrder).orderByDesc(TransparencyRecord::getMonth);
        return Result.ok(transparencyRecordService.list(wrapper));
    }

    @PostMapping("/transparency")
    @Transactional
    public Result<Boolean> createTransparency(@RequestBody TransparencyRecord payload) {
        shiftTransparencySortOrder();
        payload.setSortOrder(1);
        payload.setExpense(normalizeCurrencyInput(payload.getExpense()));
        return Result.ok(transparencyRecordService.save(payload));
    }

    @PutMapping("/transparency/{id}")
    public Result<Boolean> updateTransparency(@PathVariable Long id, @RequestBody TransparencyRecord payload) {
        payload.setId(id);
        payload.setExpense(normalizeCurrencyInput(payload.getExpense()));
        return Result.ok(transparencyRecordService.updateById(payload));
    }

    @DeleteMapping("/transparency/{id}")
    public Result<Boolean> deleteTransparency(@PathVariable Long id) {
        return Result.ok(transparencyRecordService.removeById(id));
    }

    @GetMapping("/urgent-needs")
    public Result<List<UrgentNeed>> listUrgentNeeds() {
        LambdaQueryWrapper<UrgentNeed> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(UrgentNeed::getSortOrder).orderByDesc(UrgentNeed::getId);
        return Result.ok(urgentNeedService.list(wrapper));
    }

    @PostMapping("/urgent-needs")
    @Transactional
    public Result<Boolean> createUrgentNeed(@RequestBody UrgentNeed payload) {
        shiftUrgentNeedsSortOrder();
        payload.setSortOrder(1);
        return Result.ok(urgentNeedService.save(payload));
    }

    @PutMapping("/urgent-needs/{id}")
    public Result<Boolean> updateUrgentNeed(@PathVariable Long id, @RequestBody UrgentNeed payload) {
        payload.setId(id);
        return Result.ok(urgentNeedService.updateById(payload));
    }

    @DeleteMapping("/urgent-needs/{id}")
    public Result<Boolean> deleteUrgentNeed(@PathVariable Long id) {
        return Result.ok(urgentNeedService.removeById(id));
    }

    @GetMapping("/donation-records")
    public Result<List<DonationRecord>> listDonationRecords() {
        LambdaQueryWrapper<DonationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DonationRecord::getSortOrder).orderByDesc(DonationRecord::getId);
        return Result.ok(donationRecordService.list(wrapper));
    }

    @PostMapping("/donation-records")
    @Transactional
    public Result<Boolean> createDonationRecord(@RequestBody DonationRecord payload) {
        shiftDonationRecordsSortOrder();
        payload.setSortOrder(1);
        return Result.ok(donationRecordService.save(payload));
    }

    @PutMapping("/donation-records/{id}")
    public Result<Boolean> updateDonationRecord(@PathVariable Long id, @RequestBody DonationRecord payload) {
        payload.setId(id);
        return Result.ok(donationRecordService.updateById(payload));
    }

    @DeleteMapping("/donation-records/{id}")
    public Result<Boolean> deleteDonationRecord(@PathVariable Long id) {
        return Result.ok(donationRecordService.removeById(id));
    }

    @GetMapping("/health")
    public Result<Map<String, String>> health() {
        return Result.ok(Map.of("status", "ok"));
    }

    private String normalizeCurrencyInput(String value) {
        if (value == null || value.isBlank()) {
            return "0";
        }
        String normalized = value.replaceAll("[^\\d.]", "");
        int firstDotIndex = normalized.indexOf('.');
        if (firstDotIndex == -1) {
            return normalized.isBlank() ? "0" : normalized;
        }
        String integerPart = normalized.substring(0, firstDotIndex + 1);
        String decimalPart = normalized.substring(firstDotIndex + 1).replace(".", "");
        String result = integerPart + decimalPart;
        return result.isBlank() ? "0" : result;
    }

    private void shiftWeeklyUpdatesSortOrder() {
        List<WeeklyUpdate> rows = weeklyUpdateService.list();
        if (rows.isEmpty()) {
            return;
        }
        rows.forEach(item -> item.setSortOrder((item.getSortOrder() == null ? 0 : item.getSortOrder()) + 1));
        weeklyUpdateService.updateBatchById(rows);
    }

    private void shiftTransparencySortOrder() {
        List<TransparencyRecord> rows = transparencyRecordService.list();
        if (rows.isEmpty()) {
            return;
        }
        rows.forEach(item -> item.setSortOrder((item.getSortOrder() == null ? 0 : item.getSortOrder()) + 1));
        transparencyRecordService.updateBatchById(rows);
    }

    private void shiftUrgentNeedsSortOrder() {
        List<UrgentNeed> rows = urgentNeedService.list();
        if (rows.isEmpty()) {
            return;
        }
        rows.forEach(item -> item.setSortOrder((item.getSortOrder() == null ? 0 : item.getSortOrder()) + 1));
        urgentNeedService.updateBatchById(rows);
    }

    private void shiftDonationRecordsSortOrder() {
        List<DonationRecord> rows = donationRecordService.list();
        if (rows.isEmpty()) {
            return;
        }
        rows.forEach(item -> item.setSortOrder((item.getSortOrder() == null ? 0 : item.getSortOrder()) + 1));
        donationRecordService.updateBatchById(rows);
    }


    @GetMapping("/dashboard-metrics")
    public Result<Map<String, String>> getDashboardMetrics() {
        return Result.ok(Map.of(
                "totalRescueCount", getConfigValue("dashboard.totalRescueCount", "2680"),
                "adoptionSuccessBase", getConfigValue("dashboard.adoptionSuccessCount", "1930")
        ));
    }

    @PutMapping("/dashboard-metrics")
    public Result<Boolean> updateDashboardMetrics(@RequestBody Map<String, String> payload) {
        upsertConfig("dashboard.totalRescueCount", payload.getOrDefault("totalRescueCount", "2680"), "累计救助可编辑基准值");
        upsertConfig("dashboard.adoptionSuccessCount", payload.getOrDefault("adoptionSuccessBase", "1930"), "成功领养可编辑基准值");
        return Result.ok(true);
    }

    private String getConfigValue(String key, String defaultValue) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key).last("LIMIT 1");
        SystemConfig row = systemConfigService.getOne(wrapper);
        return row == null || row.getConfigValue() == null ? defaultValue : row.getConfigValue();
    }

    private void upsertConfig(String key, String value, String description) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key).last("LIMIT 1");
        SystemConfig row = systemConfigService.getOne(wrapper);
        if (row == null) {
            row = new SystemConfig();
            row.setConfigKey(key);
            row.setConfigValue(value);
            row.setDescription(description);
            systemConfigService.save(row);
        } else {
            row.setConfigValue(value);
            row.setDescription(description);
            systemConfigService.updateById(row);
        }
    }
}
