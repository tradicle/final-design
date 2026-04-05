package com.xxx.animal.controller;

import com.xxx.animal.common.Result;
import com.xxx.animal.entity.User;
import com.xxx.animal.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser == null) {
            return Result.fail("用户名或密码错误");
        }
        return Result.ok(loginUser);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        try {
            return Result.ok(userService.register(user));
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
