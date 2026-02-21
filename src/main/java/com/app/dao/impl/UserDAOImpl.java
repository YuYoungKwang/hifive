package com.app.dao.impl;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.dao.UserDAO;

@Repository
public class UserDAOImpl implements UserDAO {
    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Override // 인터페이스의 약속을 지킨다는 뜻
    public String getUserNickname(int userNo) { // 이름을 getUserNickname으로 변경!
        // 쿼리 ID도 맞추는 것이 좋습니다. (UserMapper.xml 참고)
         return sqlSessionTemplate.selectOne("user_mapper.getUserNickname", userNo);
    }
}