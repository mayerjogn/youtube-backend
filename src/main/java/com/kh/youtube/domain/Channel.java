package com.kh.youtube.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    private int channelCode;
    private String channelName;
    private String channelPhoto;
    private String channelDesc;
    private Date channelDate;

    private Member member;
}
