package com.app.dto;

import lombok.Data;

@Data
public class CategoryDTO {
	private Long categoryNo;
	private Long userNo;
	private String categoryType;
	private String categoryIsUsed;
}
