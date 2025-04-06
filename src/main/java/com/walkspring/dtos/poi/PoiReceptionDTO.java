package com.walkspring.dtos.poi;
// Dieses DTO dient der Verarbeitung des Wikipedia-Artikels
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PoiReceptionDTO {

    private String title;
    private String body;
    private String uid_at_api;
    private double latitude;
    private double longitude;
    private String url;

}
