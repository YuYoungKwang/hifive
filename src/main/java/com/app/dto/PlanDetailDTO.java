package com.app.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class PlanDetailDTO {

    private Long planDetailNo;
    private Long planNo;
    private Long placeNo;
    private Long userNo;
    private Integer planVisitOrder;
    private String planMeno;
    private Date detailStartDate;
    private Date detailEndDate;

    // Joined fields
    private String placeName;
}
