package com.app.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PlaceReviewDTO {
	private Long commentNo;
	private Long logNo;
	private Long placeNo;
	private String placeName;
	private Integer rating;
	private String commentContent;
	private String createdByAuthId;
	private String createdBy;
	private Date createdAt;
}
