package com.app.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class CommunityDTO {

    private Long reviewNo;
    private Long planNo;
    private Long userNo;
    private String reviewContent;
    private Integer rating;
    private String createdBy;
    private Timestamp createdAt;
}
