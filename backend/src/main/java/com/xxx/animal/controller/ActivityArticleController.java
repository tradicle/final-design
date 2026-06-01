package com.xxx.animal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.animal.common.Result;
import com.xxx.animal.entity.ActivityArticle;
import com.xxx.animal.service.ActivityArticleService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class ActivityArticleController {
    private final ActivityArticleService activityArticleService;

    public ActivityArticleController(ActivityArticleService activityArticleService) {
        this.activityArticleService = activityArticleService;
    }

    @GetMapping("/api/activities")
    public Result<List<ActivityArticle>> list() {
        LambdaQueryWrapper<ActivityArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ActivityArticle::getPublishTime).orderByDesc(ActivityArticle::getId);
        return Result.ok(activityArticleService.list(wrapper));
    }

    @GetMapping("/api/activities/{id}")
    public Result<ActivityArticle> detail(@PathVariable Long id) {
        return Result.ok(activityArticleService.getById(id));
    }

    @GetMapping("/api/admin/activities")
    public Result<List<ActivityArticle>> adminList() {
        return list();
    }

    @PostMapping("/api/admin/activities")
    public Result<Boolean> create(@RequestBody ActivityArticle payload) {
        if (payload.getPublishTime() == null) {
            payload.setPublishTime(LocalDateTime.now());
        }
        return Result.ok(activityArticleService.save(payload));
    }

    @PutMapping("/api/admin/activities/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody ActivityArticle payload) {
        payload.setId(id);
        return Result.ok(activityArticleService.updateById(payload));
    }

    @DeleteMapping("/api/admin/activities/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(activityArticleService.removeById(id));
    }
}
