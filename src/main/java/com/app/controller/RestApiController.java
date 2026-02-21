package com.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.service.ImportProgressTracker;
import com.app.service.PlaceService;

@Controller
public class RestApiController {

	@Autowired
	private PlaceService placeService;

	@Autowired
	private ImportProgressTracker importProgressTracker;

	@GetMapping("/restAPICon")
	@ResponseBody
	public String restAPICon(
			@RequestParam(defaultValue = "1") int maxPages,
			@RequestParam(defaultValue = "300") int pageSize,
			@RequestParam(defaultValue = "300") int batchSize) throws Exception {
		int insertedCount = placeService.updateAreaBasedListPlaces(maxPages, pageSize, batchSize);
		return "place_inserted=" + insertedCount + ", maxPages=" + maxPages + ", pageSize=" + pageSize + ", batchSize=" + batchSize;
	}

	@GetMapping("/restAPICon/places")
	@ResponseBody
	public String restAPIConPlaces(
			@RequestParam(defaultValue = "1") int maxPages,
			@RequestParam(defaultValue = "300") int pageSize,
			@RequestParam(defaultValue = "300") int batchSize) throws Exception {
		int insertedCount = placeService.updateAreaBasedListPlaces(maxPages, pageSize, batchSize);
		return "place_inserted=" + insertedCount + ", maxPages=" + maxPages + ", pageSize=" + pageSize + ", batchSize=" + batchSize;
	}

	@GetMapping("/restAPICon/hours")
	@ResponseBody
	public String restAPIConHours(
			@RequestParam(defaultValue = "500") int batchSize,
			@RequestParam(defaultValue = "6") int parallelism) throws Exception {
		int insertedCount = placeService.updatePlaceHours(batchSize, parallelism);
		return "place_hours_inserted=" + insertedCount + ", batchSize=" + batchSize + ", parallelism=" + parallelism;
	}

	@GetMapping("/restAPICon/progress")
	@ResponseBody
	public Map<String, Object> restAPIConProgress() {
		return importProgressTracker.snapshot();
	}
}
