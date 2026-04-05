package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.Comment;
import com.xxx.animal.entity.Post;
import com.xxx.animal.mapper.CommentMapper;
import com.xxx.animal.mapper.PostMapper;
import com.xxx.animal.service.PostService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    
    private final CommentMapper commentMapper;

    public PostServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public List<Post> getList() {
        List<Post> posts = baseMapper.selectListWithUser();
        for (Post post : posts) {
            List<Comment> comments = commentMapper.selectByPostId(post.getId());
            post.setComments(comments);
        }
        return posts;
    }

    @Override
    public boolean createPost(Post post) {
        post.setStatus(1); // Default normal
        return this.save(post);
    }
}
