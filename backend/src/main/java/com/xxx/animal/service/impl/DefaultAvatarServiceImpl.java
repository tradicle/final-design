package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.DefaultAvatar;
import com.xxx.animal.mapper.DefaultAvatarMapper;
import com.xxx.animal.service.DefaultAvatarService;
import org.springframework.stereotype.Service;

@Service
public class DefaultAvatarServiceImpl extends ServiceImpl<DefaultAvatarMapper, DefaultAvatar> implements DefaultAvatarService {
}
