package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.News;
import com.xxx.animal.mapper.NewsMapper;
import com.xxx.animal.service.NewsService;
import org.springframework.stereotype.Service;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {
}
