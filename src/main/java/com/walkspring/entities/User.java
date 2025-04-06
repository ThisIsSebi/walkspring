package com.walkspring.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.walkspring.enums.ActivityStatus;
import com.walkspring.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "image_id")
    private Image userImage;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Column(nullable = false)
    private boolean enabled;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Column(nullable = true)
    @Enumerated(value = EnumType.STRING)
    private ActivityStatus status;

//    @OneToMany(mappedBy = "createdBy")
//    @JoinColumn
//    private List<Poi> poiCreated;

    @OneToMany(mappedBy ="user", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<Checkin> checkins;

    public User(String username, String email, String password, UserRole userRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.enabled = true;
    }

    //Hier werden die Berechtigungen des Users geladen WICHTIG!!! die Role ist auch eine Berechtigung
    //In diesem Beispiel ist die einzige Berechtigung die ein User hat seine Rolle man könnte es aber auch um eigene Berechtigungen erweitern
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(userRole.toString()));
        return authorityList;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
