package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.ActivityArticle;
import com.xxx.animal.mapper.ActivityArticleMapper;
import com.xxx.animal.service.ActivityArticleService;
import org.springframework.stereotype.Service;

@Service
public class ActivityArticleServiceImpl extends ServiceImpl<ActivityArticleMapper, ActivityArticle> implements ActivityArticleService {
}
