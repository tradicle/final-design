package com.xxx.animal.controller;

import com.xxx.animal.common.Result;
import com.xxx.animal.entity.User;
import com.xxx.animal.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody User user, HttpServletRequest request) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser == null) {
            return Result.fail("用户名或密码错误");
        }
        loginUser.setPassword(null);
        request.getSession().setAttribute("loginUser", loginUser);
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

    @PostMapping("/logout")
    public Result<Boolean> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Result.ok(true);
    }

    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("请先登录");
        }
        User user = userService.getProfile(loginUser.getId());
        if (user != null) {
            user.setPassword(null);
        }
        return Result.ok(user);
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("请先登录");
        }
        try {
            User updated = userService.updateProfile(
                    loginUser.getId(),
                    payload.get("nickname"),
                    payload.get("email"),
                    payload.get("avatar")
            );
            updated.setPassword(null);
            request.getSession().setAttribute("loginUser", updated);
            return Result.ok(updated);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/password")
    public Result<Boolean> changePassword(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("请先登录");
        }
        String newPassword = payload.get("newPassword");
        String confirmPassword = payload.get("confirmPassword");
        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            return Result.fail("两次输入的新密码不一致");
        }
        try {
            userService.changePassword(loginUser.getId(), payload.get("oldPassword"), newPassword);
            return Result.ok(true);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
