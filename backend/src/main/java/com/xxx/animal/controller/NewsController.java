package com.xxx.animal.controller;

import com.xxx.animal.common.Result;
import com.xxx.animal.entity.News;
import com.xxx.animal.service.NewsService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public Result<List<News>> list() {
        return Result.ok(newsService.list());
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody News news) {
        return Result.ok(newsService.save(news));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody News news) {
        news.setId(id);
        return Result.ok(newsService.updateById(news));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(newsService.removeById(id));
    }
}
