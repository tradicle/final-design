package com.xxx.animal.controller;

import com.xxx.animal.common.Result;
import com.xxx.animal.entity.Comment;
import com.xxx.animal.entity.Post;
import com.xxx.animal.service.CommentService;
import com.xxx.animal.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CommunityController {

    private final PostService postService;
    private final CommentService commentService;

    public CommunityController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping("/api/community/posts")
    public Result<List<Post>> listPosts() {
        return Result.ok(postService.getList());
    }

    @PostMapping("/api/community/posts")
    public Result<Boolean> createPost(@RequestBody Post post) {
        return Result.ok(postService.createPost(post));
    }

    @GetMapping("/api/community/posts/{postId}/comments")
    public Result<List<Comment>> listComments(@PathVariable Long postId) {
        return Result.ok(commentService.getComments(postId));
    }

    @PostMapping("/api/community/comments")
    public Result<Boolean> createComment(@RequestBody Comment comment) {
        return Result.ok(commentService.createComment(comment));
    }

    @GetMapping("/api/admin/posts")
    public Result<List<Post>> adminListPosts() {
        return Result.ok(postService.getAdminList());
    }

    @PutMapping("/api/community/posts/{id}/status")
    public Result<Boolean> setPostStatus(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Post post = postService.getById(id);
        if (post == null) {
            return Result.fail("帖子不存在");
        }
        post.setStatus(payload.getOrDefault("status", 1));
        return Result.ok(postService.updateById(post));
    }

    @DeleteMapping("/api/community/posts/{id}")
    public Result<Boolean> deletePost(@PathVariable Long id) {
        return Result.ok(postService.removeById(id));
    }

    @PutMapping("/api/community/comments/{id}/status")
    public Result<Boolean> setCommentStatus(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.fail("评论不存在");
        }
        comment.setStatus(payload.getOrDefault("status", 1));
        return Result.ok(commentService.updateById(comment));
    }

    @DeleteMapping("/api/community/comments/{id}")
    public Result<Boolean> deleteComment(@PathVariable Long id) {
        return Result.ok(commentService.removeById(id));
    }
}
