package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.Clue;
import com.xxx.animal.mapper.ClueMapper;
import com.xxx.animal.service.ClueService;
import org.springframework.stereotype.Service;

@Service
public class ClueServiceImpl extends ServiceImpl<ClueMapper, Clue> implements ClueService {
}
