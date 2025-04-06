package com.walkspring.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.walkspring.enums.ActivityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
public class Poi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int poiId;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = true, length = 1000)
    private String body;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "image_id")
    private Image poiImage;

    private String poiImageUrl;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

//    @CreatedBy
//    @ManyToOne
//    @JoinColumn(name = "created_by_user", nullable = true)
//    private User createdBy;

    @OneToMany(mappedBy = "poi", cascade = CascadeType.REMOVE)
    private List<Checkin> checkins;

    @Column(length = 255) // hatte ich zuerst auf unique=true, um doppeltes Speichern zu verhindern, evtl. unnötig
    private String pageid; // ACHTUNG: Das ist NICHT die fortlaufende Datenbank-ID, sondern die Page-ID von Wikipedia-Artikeln (schreibt sich auch mit kleinem i)

    @Column(length = 2083)
    private String url;

    @OneToMany(mappedBy = "poi", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Checkin> checkinList;

    @Enumerated(EnumType.STRING)
    private ActivityStatus status;

    private boolean userGenerated;

    // Constructor für Datenbankspeicherung mit den "nötigsten" Spalten
    public Poi(String pageid, String title, double latitude, double longitude) {
        this.pageid = pageid;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userGenerated = false;
    }

//    Constructor für usergenerierte POIs


    public Poi(String title, String body, double latitude, double longitude, String url) {
        this.title = title;
        this.body = body;
        this.latitude = latitude;
        this.longitude = longitude;
        this.url = url;
        this.userGenerated = true;
    }
}
