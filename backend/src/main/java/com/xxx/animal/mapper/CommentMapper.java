package com.xxx.animal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.animal.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    @Select("SELECT c.*, u.username, u.avatar FROM comment c LEFT JOIN sys_user u ON c.user_id = u.id WHERE c.post_id = #{postId} AND c.status = 1 ORDER BY c.create_time ASC")
    List<Comment> selectByPostId(Long postId);
}
