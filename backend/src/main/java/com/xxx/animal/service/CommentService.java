package com.xxx.animal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.animal.entity.Comment;
import java.util.List;

public interface CommentService extends IService<Comment> {
    List<Comment> getComments(Long postId);
    boolean createComment(Comment comment);
}
