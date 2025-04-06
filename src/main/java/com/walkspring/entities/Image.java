package com.walkspring.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;

    private String path;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdDate;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "thumbnail_id")
    private Image thumbnail;

    @OneToOne(mappedBy = "userImage")
    private User user;

    @OneToOne(mappedBy = "poiImage")
    private Poi poi;

    @OneToOne(mappedBy = "checkinImage")
    private Checkin checkin;

}
