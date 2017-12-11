package org.learn.open.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;

public interface UserInfoMapper {
	 Map<String,Object> findByUsername(@Param("username")String username);
	
	 Page<Map<String,Object>> pageList(@Param("username")String username);
}
