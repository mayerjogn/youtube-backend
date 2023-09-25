package com.kh.youtube.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO { // 데이터 이동 시킬 껍데기

    private String token;
    private String id;
    private String password;
    private String name;

}
