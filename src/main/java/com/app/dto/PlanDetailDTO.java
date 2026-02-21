package com.app.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class PlanDetailDTO {

    private Long planDetailNo;
    private Long planNo;
    private Long placeNo;
    private Long userNo;
    private Integer planVisitOrder;
    private String planMeno;
    private Timestamp detailStartDate;
    private Timestamp detailEndDate;

    // Joined fields
    private String placeName;
    private String placeAddress;
    private Double placeLatitude;
    private Double placeLongitude;
}
