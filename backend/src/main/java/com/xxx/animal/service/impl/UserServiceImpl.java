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
        // Set default role
        user.setRole("USER");
        
        baseMapper.insert(user);
        return user;
    }
}
