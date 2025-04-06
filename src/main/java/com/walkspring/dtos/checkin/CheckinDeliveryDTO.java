package com.walkspring.dtos.checkin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CheckinDeliveryDTO {

    private int poiId;
    private int userId;
    private String username;
    private int userImageId;
    private String note;

}
