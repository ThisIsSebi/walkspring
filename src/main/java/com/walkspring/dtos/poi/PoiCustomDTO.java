package com.walkspring.dtos.poi;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class PoiCustomDTO {

    private final String title;
    private final String body;
    private final double latitude;
    private final double longitude;
    private String url;
//    private String poiImage;

}
