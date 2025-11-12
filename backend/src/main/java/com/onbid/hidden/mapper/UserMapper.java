package com.onbid.mapper;

import com.onbid.domain.User;
import org.apache.ibatis.annotations.*;

/**
 * 사용자 MyBatis Mapper (어노테이션 방식)
 */
@Mapper
public interface UserMapper {
    
    /**
     * 사용자 등록
     * @param user 사용자 정보
     */
    @Insert("INSERT INTO users (email, password, username, created_at, updated_at) " +
            "VALUES (#{email}, #{password}, #{username}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
    
    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return 사용자 정보
     */
    @Select("SELECT id, email, password, username, created_at, updated_at " +
            "FROM users WHERE email = #{email}")
    User findByEmail(String email);
    
    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    @Select("SELECT id, email, password, username, created_at, updated_at " +
            "FROM users WHERE id = #{id}")
    User findById(Long id);
    
    /**
     * 사용자 정보 수정
     * @param user 수정할 사용자 정보
     */
    @Update("UPDATE users SET username = #{username}, password = #{password}, updated_at = NOW() " +
            "WHERE id = #{id}")
    void update(User user);
    
    /**
     * 사용자 삭제
     * @param id 사용자 ID
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    void delete(Long id);
    
    /**
     * 이메일 중복 체크
     * @param email 이메일
     * @return 존재 여부 (1: 존재, 0: 없음)
     */
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int existsByEmail(String email);
}

