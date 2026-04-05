package com.xxx.animal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.animal.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    @Select("SELECT p.*, u.username, u.avatar FROM post p LEFT JOIN sys_user u ON p.user_id = u.id WHERE p.status = 1 ORDER BY p.create_time DESC")
    List<Post> selectListWithUser();
}
