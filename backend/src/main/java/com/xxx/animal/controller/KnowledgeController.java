package com.xxx.animal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.animal.common.Result;
import com.xxx.animal.entity.Knowledge;
import com.xxx.animal.service.KnowledgeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @GetMapping
    public Result<List<Knowledge>> list() {
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Knowledge::getSortOrder).orderByAsc(Knowledge::getId);
        return Result.ok(knowledgeService.list(wrapper));
    }

    @GetMapping("/{id}")
    public Result<Knowledge> detail(@PathVariable Long id) {
        return Result.ok(knowledgeService.getById(id));
    }
}
