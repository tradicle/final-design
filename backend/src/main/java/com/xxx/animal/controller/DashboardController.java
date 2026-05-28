package com.xxx.animal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.animal.common.Result;
import com.xxx.animal.entity.AdoptionApplication;
import com.xxx.animal.entity.Animal;
import com.xxx.animal.entity.DonationClaim;
import com.xxx.animal.entity.News;
import com.xxx.animal.entity.SystemConfig;
import com.xxx.animal.entity.TransparencyRecord;
import com.xxx.animal.entity.WeeklyUpdate;
import com.xxx.animal.service.AdoptionApplicationService;
import com.xxx.animal.service.AnimalService;
import com.xxx.animal.service.DonationClaimService;
import com.xxx.animal.service.NewsService;
import com.xxx.animal.service.SystemConfigService;
import com.xxx.animal.service.TransparencyRecordService;
import com.xxx.animal.service.WeeklyUpdateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final AnimalService animalService;
    private final AdoptionApplicationService adoptionApplicationService;
    private final DonationClaimService donationClaimService;
    private final NewsService newsService;
    private final WeeklyUpdateService weeklyUpdateService;
    private final TransparencyRecordService transparencyRecordService;
    private final SystemConfigService systemConfigService;

    public DashboardController(
            AnimalService animalService,
            AdoptionApplicationService adoptionApplicationService,
            DonationClaimService donationClaimService,
            NewsService newsService,
            WeeklyUpdateService weeklyUpdateService,
            TransparencyRecordService transparencyRecordService,
            SystemConfigService systemConfigService
    ) {
        this.animalService = animalService;
        this.adoptionApplicationService = adoptionApplicationService;
        this.donationClaimService = donationClaimService;
        this.newsService = newsService;
        this.weeklyUpdateService = weeklyUpdateService;
        this.transparencyRecordService = transparencyRecordService;
        this.systemConfigService = systemConfigService;
    }

    @GetMapping("/summary")
    public Result<Map<String, Object>> summary() {
        long availableCount = animalService.count(new LambdaQueryWrapper<Animal>().eq(Animal::getStatus, 1));
        long totalProfileCount = animalService.count();
        // 机构经营指标：展示可信度与规模，采用“历史累计 + 当前数据库动态”混合口径
        String monthlyBudget = transparencyRecordService.list().stream()
                .max(Comparator.comparing(TransparencyRecord::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .map(TransparencyRecord::getExpense)
                .orElse("¥0");
        int totalRescueCount = getIntConfig("dashboard.totalRescueCount", 2680);
        int adoptionSuccessCount = getIntConfig("dashboard.adoptionSuccessCount", 1930);
        Map<String, Object> data = Map.of(
                "totalRescueCount", totalRescueCount,
                "adoptionSuccessCount", adoptionSuccessCount,
                "activeAnimals", availableCount,
                "totalProfiles", totalProfileCount,
                "monthlyPublicBudget", monthlyBudget
        );
        return Result.ok(data);
    }

    @GetMapping("/weekly-updates")
    public Result<List<Map<String, String>>> weeklyUpdates() {
        LambdaQueryWrapper<WeeklyUpdate> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(WeeklyUpdate::getSortOrder).orderByDesc(WeeklyUpdate::getId);
        List<Map<String, String>> updates = weeklyUpdateService.list(wrapper).stream()
                .map(i -> Map.of("title", i.getTitle(), "desc", i.getDescription()))
                .collect(Collectors.toList());
        return Result.ok(updates);
    }

    @GetMapping("/transparency")
    public Result<List<Map<String, String>>> transparency() {
        LambdaQueryWrapper<TransparencyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(TransparencyRecord::getSortOrder).orderByDesc(TransparencyRecord::getMonth);
        List<Map<String, String>> rows = transparencyRecordService.list(wrapper).stream()
                .map(i -> Map.of(
                        "month", i.getMonth(),
                        "income", i.getIncome(),
                        "expense", i.getExpense(),
                        "note", i.getNote()))
                .collect(Collectors.toList());
        return Result.ok(rows);
    }

    @GetMapping("/charts")
    public Result<Map<String, Object>> charts() {
        long catCount = animalService.count(new LambdaQueryWrapper<Animal>().eq(Animal::getCategory, "CAT"));
        long dogCount = animalService.count(new LambdaQueryWrapper<Animal>().eq(Animal::getCategory, "DOG"));
        long availableAnimalCount = animalService.count(new LambdaQueryWrapper<Animal>().eq(Animal::getStatus, 1));
        long adoptedAnimalCount = animalService.count(new LambdaQueryWrapper<Animal>().eq(Animal::getStatus, 0));
        long pendingAdoptionCount = adoptionApplicationService.count(new LambdaQueryWrapper<AdoptionApplication>().eq(AdoptionApplication::getStatus, 0));
        long approvedAdoptionCount = adoptionApplicationService.count(new LambdaQueryWrapper<AdoptionApplication>().eq(AdoptionApplication::getStatus, 1));
        long rejectedAdoptionCount = adoptionApplicationService.count(new LambdaQueryWrapper<AdoptionApplication>().eq(AdoptionApplication::getStatus, 2));
        long pendingClaimCount = donationClaimService.count(new LambdaQueryWrapper<DonationClaim>().eq(DonationClaim::getStatus, 0));
        long approvedClaimCount = donationClaimService.count(new LambdaQueryWrapper<DonationClaim>().eq(DonationClaim::getStatus, 1));
        long rejectedClaimCount = donationClaimService.count(new LambdaQueryWrapper<DonationClaim>().eq(DonationClaim::getStatus, 2));
        long newsCount = newsService.count();

        Map<String, Object> data = Map.of(
                "animalCategoryStats", List.of(
                        Map.of("label", "猫咪档案", "value", catCount),
                        Map.of("label", "狗狗档案", "value", dogCount)
                ),
                "animalStatusStats", List.of(
                        Map.of("label", "待领养", "value", availableAnimalCount),
                        Map.of("label", "已领养", "value", adoptedAnimalCount)
                ),
                "adoptionStatusStats", List.of(
                        Map.of("label", "待审核", "value", pendingAdoptionCount),
                        Map.of("label", "已通过", "value", approvedAdoptionCount),
                        Map.of("label", "已拒绝", "value", rejectedAdoptionCount)
                ),
                "donationClaimStatusStats", List.of(
                        Map.of("label", "待审核", "value", pendingClaimCount),
                        Map.of("label", "已通过", "value", approvedClaimCount),
                        Map.of("label", "已拒绝", "value", rejectedClaimCount)
                ),
                "overviewStats", List.of(
                        Map.of("label", "资讯总数", "value", newsCount),
                        Map.of("label", "每周更新", "value", (long) weeklyUpdateService.count()),
                        Map.of("label", "领养申请总量", "value", (long) adoptionApplicationService.count()),
                        Map.of("label", "物资认领总量", "value", (long) donationClaimService.count())
                )
        );
        return Result.ok(data);
    }

    private int getIntConfig(String key, int defaultValue) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key).last("LIMIT 1");
        SystemConfig row = systemConfigService.getOne(wrapper);
        if (row == null || row.getConfigValue() == null) return defaultValue;
        try {
            return Integer.parseInt(row.getConfigValue());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}
