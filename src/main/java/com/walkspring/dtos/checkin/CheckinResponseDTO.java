package com.walkspring.dtos.checkin;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class CheckinResponseDTO {

    private CheckinPoiDTO checkinPoi;
    private final int userId;
    private final String username;
    private int imageId;
    private String note;
    private final LocalDateTime visitedAt;
    private final boolean visible;

}

/*


@IdClass(Checkin_PK.class)
public class Checkin {

    @Id
    @ManyToOne
    @JoinColumn(name = "poi_id")
    private Poi poi;

    @Id
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String note;

    @Column(updatable = false)
    private LocalDateTime visitedAt = LocalDateTime.now();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private CheckinStatus visibility;

}

 */