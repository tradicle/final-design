package com.xxx.animal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.animal.common.Result;
import com.xxx.animal.entity.ActivityArticle;
import com.xxx.animal.service.ActivityArticleService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/api/admin/activities/import-word")
    public Result<Map<String, String>> importWord(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.fail("请选择 Word 文件");
        }

        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            StringBuilder html = new StringBuilder();
            String title = null;
            String summary = null;

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (!StringUtils.hasText(text)) {
                    continue;
                }
                String escaped = HtmlUtils.htmlEscape(text.trim());
                if (!StringUtils.hasText(title)) {
                    title = text.trim();
                    html.append("<h3>").append(escaped).append("</h3>");
                    continue;
                }
                if (!StringUtils.hasText(summary)) {
                    summary = text.trim();
                }
                html.append("<p>").append(escaped).append("</p>");
            }

            if (!StringUtils.hasText(title)) {
                String originalFilename = file.getOriginalFilename();
                title = StringUtils.hasText(originalFilename) ? originalFilename.replaceFirst("\\.[^.]+$", "") : "未命名活动";
            }
            if (!StringUtils.hasText(summary)) {
                summary = title;
            }

            Map<String, String> result = new LinkedHashMap<>();
            result.put("title", title);
            result.put("summary", summary.length() > 120 ? summary.substring(0, 120) : summary);
            result.put("content", html.toString());
            return Result.ok(result);
        }
    }
}
