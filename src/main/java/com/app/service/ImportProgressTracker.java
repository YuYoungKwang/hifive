package com.app.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

@Component
public class ImportProgressTracker {

	private final AtomicInteger insertedCount = new AtomicInteger(0);
	private final AtomicInteger processedCount = new AtomicInteger(0);

	private volatile boolean running = false;
	private volatile String status = "IDLE";
	private volatile String jobType = "";
	private volatile String message = "";
	private volatile int currentPage = 0;
	private volatile int pageSize = 0;
	private volatile int maxPages = 0;
	private volatile int totalTargets = 0;
	private volatile long startedAtMillis = 0L;
	private volatile long updatedAtMillis = 0L;
	private volatile long endedAtMillis = 0L;

	public synchronized void startPlaceImport(int pageSize, int maxPages) {
		resetBase("RUNNING", "PLACES");
		this.pageSize = pageSize;
		this.maxPages = maxPages;
		this.message = "Place import started";
	}

	public synchronized void startHoursImport(int totalTargets, int batchSize) {
		resetBase("RUNNING", "HOURS");
		this.pageSize = batchSize;
		this.totalTargets = totalTargets;
		this.message = "Hours import started";
	}

	public void onPlacePageFetched(int pageNo, int fetchedCount) {
		this.currentPage = pageNo;
		processedCount.addAndGet(Math.max(0, fetchedCount));
		this.message = "Place API page " + pageNo + " fetched (" + fetchedCount + " rows)";
		touch();
	}

	public void onHoursPlaceProcessed(int processedPlaces) {
		this.currentPage = processedPlaces;
		processedCount.set(Math.max(0, processedPlaces));
		this.message = "Hours processing (" + processedPlaces + "/" + totalTargets + ")";
		touch();
	}

	public void addInserted(int delta) {
		if (delta <= 0) {
			return;
		}
		insertedCount.addAndGet(delta);
		touch();
	}

	public synchronized void complete(String message) {
		this.running = false;
		this.status = "COMPLETED";
		this.message = message;
		this.endedAtMillis = System.currentTimeMillis();
		this.updatedAtMillis = this.endedAtMillis;
	}

	public synchronized void fail(Exception e) {
		this.running = false;
		this.status = "FAILED";
		this.message = e == null ? "Error occurred" : e.getClass().getSimpleName() + ": " + safeMessage(e.getMessage());
		this.endedAtMillis = System.currentTimeMillis();
		this.updatedAtMillis = this.endedAtMillis;
	}

	public Map<String, Object> snapshot() {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("running", running);
		result.put("status", status);
		result.put("jobType", jobType);
		result.put("message", message);
		result.put("insertedCount", insertedCount.get());
		result.put("processedCount", processedCount.get());
		result.put("currentPageOrIndex", currentPage);
		result.put("pageSizeOrBatchSize", pageSize);
		result.put("maxPages", maxPages);
		result.put("totalTargets", totalTargets);
		result.put("startedAtMillis", startedAtMillis);
		result.put("updatedAtMillis", updatedAtMillis);
		result.put("endedAtMillis", endedAtMillis);
		return result;
	}

	private synchronized void resetBase(String status, String jobType) {
		this.running = true;
		this.status = status;
		this.jobType = jobType;
		this.message = "";
		this.currentPage = 0;
		this.pageSize = 0;
		this.maxPages = 0;
		this.totalTargets = 0;
		this.startedAtMillis = System.currentTimeMillis();
		this.updatedAtMillis = this.startedAtMillis;
		this.endedAtMillis = 0L;
		this.insertedCount.set(0);
		this.processedCount.set(0);
	}

	private void touch() {
		this.updatedAtMillis = System.currentTimeMillis();
	}

	private String safeMessage(String message) {
		if (message == null) {
			return "";
		}
		String trimmed = message.trim();
		return trimmed.length() > 300 ? trimmed.substring(0, 300) : trimmed;
	}
}
