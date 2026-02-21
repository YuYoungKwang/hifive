package com.app.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourResponseDTO {
    private Response response;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // <-- 1. 여기에 이게 꼭 있어야 header 에러가 안 납니다!
    public static class Response {
        private Body body;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // <-- 2. 안전을 위해 여기도 추가
    public static class Body {
        private Items items;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // <-- 3. 여기도 추가
    public static class Items {
        private List<PlaceDto> item;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlaceDto {
        private String title;       // PLACE_NAME
        private String contenttypeid; // PLACE_CATEGORY
        private String cat1;
        private String cat2;
        private String cat3;
        private String addr1;       // PLACE_ADDRESS
        private String mapy;        // PLACE_LATITUDE
        private String mapx;        // PLACE_LONGITUDE
        private String tel;         // PLACE_NUMBER
        private String firstimage2;  // PLACE_THUMBNAIL_URL
    }
}
