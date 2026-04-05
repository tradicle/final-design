package com.xxx.animal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.animal.entity.Post;
import java.util.List;

public interface PostService extends IService<Post> {
    List<Post> getList();
    boolean createPost(Post post);
}
