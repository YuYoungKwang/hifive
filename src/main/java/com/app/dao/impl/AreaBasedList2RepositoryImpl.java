package com.app.dao.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.dao.AreaBasedList2Repository;
import com.app.dto.TourResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class AreaBasedList2RepositoryImpl implements AreaBasedList2Repository {

	private static final String API_URL = "https://apis.data.go.kr/B551011/KorService2/areaBasedList2";
	private static final String SERVICE_KEY = "721165d2fd5e42df4a23b761e4ae503eed80e61c9743082e420b2a7dfa55f34b";

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public List<TourResponseDTO.PlaceDto> requestApi_areaBasedList2(int pageNo, int numOfRows) throws Exception {
		StringBuilder urlBuilder = new StringBuilder(API_URL);
		urlBuilder.append("?").append(encode("serviceKey")).append("=").append(SERVICE_KEY);
		urlBuilder.append("&").append(encode("numOfRows")).append("=").append(encode(Integer.toString(numOfRows)));
		urlBuilder.append("&").append(encode("pageNo")).append("=").append(encode(Integer.toString(pageNo)));
		urlBuilder.append("&").append(encode("MobileOS")).append("=").append(encode("WEB"));
		urlBuilder.append("&").append(encode("MobileApp")).append("=").append(encode("HashTrip"));
		urlBuilder.append("&").append(encode("_type")).append("=").append(encode("json"));

		HttpURLConnection conn = (HttpURLConnection) new URL(urlBuilder.toString()).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");

		BufferedReader rd = null;
		try {
			if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
			}

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			TourResponseDTO responseData = objectMapper.readValue(sb.toString(), TourResponseDTO.class);
			if (responseData.getResponse() != null
					&& responseData.getResponse().getBody() != null
					&& responseData.getResponse().getBody().getItems() != null
					&& responseData.getResponse().getBody().getItems().getItem() != null) {
				return responseData.getResponse().getBody().getItems().getItem();
			}
			return Collections.emptyList();
		} finally {
			if (rd != null) {
				rd.close();
			}
			conn.disconnect();
		}
	}

	private String encode(String value) throws Exception {
		return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
	}
}
