package com.walkspring.dtos.poi;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
// Dieses DTO dient dazu, die Artikel "im Umkreis" zu sammeln
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PoiCollectionDTO {

    private String pageid;
    private String title;
    private double latitude;
    private double longitude;

}