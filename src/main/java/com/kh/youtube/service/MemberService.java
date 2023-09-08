package com.kh.youtube.service;

import com.kh.youtube.domain.Member;
import com.kh.youtube.repo.MemberDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j  // lombok에서 제공하는 어노테이션 log찍기 편해짐
@Service 
public class MemberService {
    
   @Autowired
    private MemberDAO dao;

   public List<Member> showAll(){
       return dao.findAll();
       // SELECT * FROM MEMBER와 같음 알아서 해주는것
   }

   public Member show(String id){
       return dao.findById(id).orElse(null);
       // SELECT * FROM MEMBER WHERE id=? 같음
   }

   public Member create(Member member){
       log.info("member : " + member); // @Slf4j 어노테이션 활용법
       return dao.save(member);
       // INSERT INTO MEMBER(ID, PASSWORD, NAME, AUTHORITY 자동으로 생성해줌
       // VALUES(?, ?, ?, 'ROLE_USER')
   }

   public Member update(Member member){
       Member target = dao.findById(member.getId()).orElse(null);
       if(target!=null){
           return dao.save(target);
       }return null;
       // UPDATE MEMBER SET ID=?, PASSWORD=?, NAME=?, AUTHORITY=?
       // WHERE ID=?
   }

   public Member delete(String id){
       Member target = dao.findById(id).orElse(null);
       // 엔티티가 필요한데 String id값이라 target으로 지정해줌
       return target;
       // DELETE FROM MEMBER WHERE ID=?
   }

}
