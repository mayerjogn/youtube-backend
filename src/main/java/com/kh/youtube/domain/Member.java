package com.kh.youtube.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert // 디폴트값이 있는경우  추가할 때 default값이 자동으로 들어가는 이노테이션
//@DynamicUpdate // authority 시큐리티 때문에 위 insert와 함께씀
public class Member {

    @Id // primary키에는 @Id
    private String id;
    // 나머지 컬럼들은 @Column
    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String authority;


}
