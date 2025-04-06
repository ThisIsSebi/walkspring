package com.walkspring.dtos.checkin;

import com.walkspring.entities.Poi;
import com.walkspring.enums.CheckinStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CheckinRequestDTO {

    private double longitude;
    private double latitude;
    private String note;
    private boolean visible;

}
