package com.walkspring.dtos.checkin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckinPoiDTO {
    private int poiId;
    private String poiTitle;
    private String poiBody;
    private String poiImageUrl;
    private double latitude;
    private double longitude;
}
