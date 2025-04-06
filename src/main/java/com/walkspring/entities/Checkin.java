package com.walkspring.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter

@IdClass(Checkin_PK.class)
public class Checkin {

    @Id
    @ManyToOne
    @JoinColumn(name = "poiId")
    private Poi poi;

    @Id
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String checkinNote;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "imageId")
    private Image checkinImage;

    @Column(updatable = false)
    private LocalDateTime visitedAt = LocalDateTime.now();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private boolean visible;

    public Checkin(Poi poi, String note, boolean visible) {
        this.poi = poi;
        this.checkinNote = note;
        this.visible = visible;
    }
}
