package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.Comment;
import com.xxx.animal.mapper.CommentMapper;
import com.xxx.animal.service.CommentService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public List<Comment> getComments(Long postId) {
        return baseMapper.selectByPostId(postId);
    }

    @Override
    public boolean createComment(Comment comment) {
        comment.setStatus(1);
        return this.save(comment);
    }
}
