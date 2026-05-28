package com.xxx.animal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.animal.common.Result;
import com.xxx.animal.entity.DefaultAvatar;
import com.xxx.animal.service.DefaultAvatarService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/default-avatars")
public class DefaultAvatarController {
    private final DefaultAvatarService defaultAvatarService;

    public DefaultAvatarController(DefaultAvatarService defaultAvatarService) {
        this.defaultAvatarService = defaultAvatarService;
    }

    @GetMapping
    public Result<List<DefaultAvatar>> list() {
        LambdaQueryWrapper<DefaultAvatar> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DefaultAvatar::getSortOrder, DefaultAvatar::getId);
        return Result.ok(defaultAvatarService.list(wrapper));
    }
}
