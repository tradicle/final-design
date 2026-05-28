package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.User;
import com.xxx.animal.mapper.UserMapper;
import com.xxx.animal.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        // In real world, use BCrypt or similar. Here we use simple equals or MD5 if you want.
        // For compatibility with previous init.sql (plain text '123456'), let's try plain text first.
        // If you want better security, we should hash passwords.
        // Given the init.sql has plain text passwords, I will stick to plain text for now to avoid breaking existing users.
        wrapper.eq(User::getPassword, password);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public User register(User user) {
        // Check if username exists
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (baseMapper.exists(wrapper)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // Set default avatar if null
        if (user.getAvatar() == null) {
            user.setAvatar("https://api.dicebear.com/9.x/avataaars/svg?seed=" + user.getUsername());
        }
        if (user.getNickname() == null || user.getNickname().isBlank()) {
            user.setNickname(user.getUsername());
        }
        // Set default role
        user.setRole("USER");
        
        baseMapper.insert(user);
        return user;
    }

    @Override
    public User getProfile(Long userId) {
        return baseMapper.selectById(userId);
    }

    @Override
    public User updateProfile(Long userId, String nickname, String email, String avatar) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }

        String nextNickname = nickname == null ? "" : nickname.trim();
        if (nextNickname.isEmpty()) {
            throw new RuntimeException("昵称不能为空");
        }

        user.setNickname(nextNickname);
        user.setEmail(email == null || email.isBlank() ? null : email.trim());
        user.setAvatar(avatar == null || avatar.isBlank() ? user.getAvatar() : avatar.trim());
        baseMapper.updateById(user);
        return baseMapper.selectById(userId);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }
        if (oldPassword == null || !oldPassword.equals(user.getPassword())) {
            throw new RuntimeException("原密码不正确");
        }

        String nextPassword = newPassword == null ? "" : newPassword.trim();
        if (nextPassword.length() < 6) {
            throw new RuntimeException("新密码长度不能少于6位");
        }
        if (nextPassword.equals(oldPassword)) {
            throw new RuntimeException("新密码不能与原密码相同");
        }

        user.setPassword(nextPassword);
        baseMapper.updateById(user);
    }
}
