package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.Knowledge;
import com.xxx.animal.mapper.KnowledgeMapper;
import com.xxx.animal.service.KnowledgeService;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, Knowledge> implements KnowledgeService {
}
