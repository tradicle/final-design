package com.xxx.animal.controller;

import com.xxx.animal.common.Result;
import com.xxx.animal.entity.Comment;
import com.xxx.animal.entity.Post;
import com.xxx.animal.service.CommentService;
import com.xxx.animal.service.PostService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final PostService postService;
    private final CommentService commentService;

    public CommunityController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping("/posts")
    public Result<List<Post>> listPosts() {
        return Result.ok(postService.getList());
    }

    @PostMapping("/posts")
    public Result<Boolean> createPost(@RequestBody Post post) {
        return Result.ok(postService.createPost(post));
    }

    @GetMapping("/posts/{postId}/comments")
    public Result<List<Comment>> listComments(@PathVariable Long postId) {
        return Result.ok(commentService.getComments(postId));
    }

    @PostMapping("/comments")
    public Result<Boolean> createComment(@RequestBody Comment comment) {
        return Result.ok(commentService.createComment(comment));
    }
}
