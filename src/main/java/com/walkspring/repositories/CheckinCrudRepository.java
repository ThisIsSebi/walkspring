package com.walkspring.repositories;

import com.walkspring.entities.Poi;
import com.walkspring.entities.Checkin;
import com.walkspring.entities.Checkin_PK;
import com.walkspring.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CheckinCrudRepository extends JpaRepository<Checkin, Checkin_PK> {

    boolean existsByPoiAndUser(Poi poi, User user);

    Optional<Checkin> findByPoiAndUser(Poi poi, User user);

    List<Checkin> findByUser(User user);

    List<Checkin> findByPoi(Poi poi);


    void deleteAllByUser(User user);

    void deleteAllByPoi(Poi poi);

    void deleteByPoiAndUser(Poi poi, User user);
}
