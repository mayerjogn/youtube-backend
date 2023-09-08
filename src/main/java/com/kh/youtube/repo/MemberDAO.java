package com.kh.youtube.repo;

import com.kh.youtube.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDAO extends JpaRepository<Member,String> { 
    // 첫번째는 사용할 vo 두번째는 primary키의 데이터타입
}
