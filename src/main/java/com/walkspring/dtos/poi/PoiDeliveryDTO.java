package com.walkspring.dtos.poi;
// Dieses DTO dient dazu, Artikel- bzw. POI-Information ans Frontend zu liefern
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class PoiDeliveryDTO {

    private final int poiId;
    private final String title;
    private String pageId;
    private final String body;
    private int imageId;
    private final double latitude;
    private final double longitude;
    private String url;
    private String imageUrl;

}
