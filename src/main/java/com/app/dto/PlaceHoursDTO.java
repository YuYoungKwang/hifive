package com.app.dto;

import lombok.Data;

@Data
public class PlaceHoursDTO {
	private Long hoursId;
	private Long placeNo;
	private Integer dayOfWeek;
	private String openTime;
	private String closeTime;
	private String breakStratTime;
	private String breakEndTime;
	private String lastOrder;
	private String isClosed;
}
