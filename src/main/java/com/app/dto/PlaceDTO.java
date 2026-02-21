package com.app.dto;

import lombok.Data;

@Data
public class PlaceDTO {
	private Long placeNo;
	private String placeName;
	private String placeCategory;
	private String placeAddress;
	private Double placeLatitude;
	private Double placeLongitude;
	private Double placeRating;
	private String placeNumber;
	private String placeThumbnailUrl;
}
