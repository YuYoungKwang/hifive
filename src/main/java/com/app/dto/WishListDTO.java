package com.app.dto;

import java.util.Date;

import lombok.Data;

@Data
public class WishListDTO {
	private Long wishNo;
	private Long placeNo;
	private Long categoryNo;
	private Long userNo;
	private Date wishDate;
	private String categoryType;
	private String categoryIsUsed;
	private String placeName;
	private String placeAddress;
	private Double placeLatitude;
	private Double placeLongitude;
	private String placeThumbnailUrl;
}
