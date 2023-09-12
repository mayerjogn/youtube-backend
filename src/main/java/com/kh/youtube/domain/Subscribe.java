package com.kh.youtube.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Data
@Entity
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
public class Subscribe {

    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "subSequence")
    @SequenceGenerator(name = "subSequence", sequenceName = "SEQ_SUBSCRIBE", allocationSize = 1)
    @Id
    @Column(name = "subs_code")
    private int subsCode;

    @Column(name = "subs_date")
    private Date subsDate;

    @ManyToOne
    @JoinColumn(name = "id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "channel_code")
    private Channel channel;
}
