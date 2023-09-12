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
public class VideoLike {

    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "vLikeSequence")
    @SequenceGenerator(name = "vLikeSequence",sequenceName = "SEQ_VIDEO_LIKE",allocationSize = 1)
    @Id
    @Column(name = "v_like_code")
    private int vLikeCode;

    @Column(name = "v_like_date")
    private Date vLikeDate;

    @ManyToOne
    @JoinColumn(name = "video_code")
    private Video video;

    @ManyToOne
    @JoinColumn(name = "id")
    private Member member;
}
