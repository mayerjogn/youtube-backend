package com.kh.youtube.service;

import com.kh.youtube.domain.Member;
import com.kh.youtube.repo.MemberDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MemberService {

    @Autowired
    private MemberDAO dao;

    public List<Member> showAll() {
        return dao.findAll();
    }

    public Member show(String id) {
        return dao.findById(id).orElse(null);
    }

    public Member create(Member member) {
        return dao.save(member); // INSERT INTO MEMBER VALUES(? ,?, ?, 'ROLE_USER')
    }

    // UPDATE MEMBER SET ID=?, PASSWORD=?, NAME=?, AUTHORITY=? WHERE ID=?
    public Member update(Member member) {
        Member target = dao.findById(member.getId()).orElse(null);
        if(target!=null) {
            return dao.save(member);
        }
        return null;
    }

    // DELETE FROM MEMBER WHERE ID=?
    public Member delete(String id) {
        Member target = show(id);
        dao.delete(target);
        return target;
    }

    public Member getByCredentials(String id, String password, PasswordEncoder encoder) {
        Member member = dao.findById(id).orElse(null);
        if(member!=null && encoder.matches(password, member.getPassword())) {
            return member;
        }
        return null;
    }
}