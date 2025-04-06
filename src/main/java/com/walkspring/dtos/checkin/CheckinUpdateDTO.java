package com.walkspring.dtos.checkin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CheckinUpdateDTO {

    private String checkinNote;
    private String checkinImage;
    private boolean visible;

}
