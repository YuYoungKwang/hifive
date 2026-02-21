package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.service.PlaceService;

@Controller
public class RestApiController {

	@Autowired
	private PlaceService placeService;

	@GetMapping("/restAPICon")
	@ResponseBody
	public String restAPICon(
			@RequestParam(defaultValue = "1") int maxPages,
			@RequestParam(defaultValue = "1000") int pageSize,
			@RequestParam(defaultValue = "300") int batchSize) throws Exception {
		int insertedCount = placeService.updateAreaBasedListPlaces(maxPages, pageSize, batchSize);
		return "inserted=" + insertedCount + ", maxPages=" + maxPages + ", pageSize=" + pageSize + ", batchSize=" + batchSize;
	}
}
