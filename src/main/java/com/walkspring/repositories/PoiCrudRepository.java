package com.walkspring.repositories;

import com.walkspring.entities.Poi;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PoiCrudRepository extends CrudRepository<Poi, Integer> {

    boolean existsByPageid(String pageid);

    Optional<Poi> findById(int poiId);

    Optional<Poi> findByLatitudeAndLongitude(double latitude, double longitude);

    public void deletePoiByLatitudeAndLongitude(double latitude, double longitude);

    public void deletePoiByPoiId(int pageid);

    @Query(value = """
            SELECT * FROM POI p
               WHERE 111.111 * DEGREES(ACOS(LEAST(1.0,
                          COS(RADIANS(p.LATITUDE)) * COS(RADIANS(:latitude))
                          * COS(RADIANS(p.LONGITUDE - :longitude))
                          + SIN(RADIANS(p.LATITUDE)) * SIN(RADIANS(:latitude))))) < :radius
               ORDER BY 111.111 * DEGREES(ACOS(LEAST(1.0,
                          COS(RADIANS(p.LATITUDE)) * COS(RADIANS(:latitude))
                          * COS(RADIANS(p.LONGITUDE - :longitude))
                          + SIN(RADIANS(p.LATITUDE)) * SIN(RADIANS(:latitude)))))
               """, nativeQuery = true)
    List<Poi> findPoisNearLocation(@Param("latitude") double latitude,
                                   @Param("longitude") double longitude,
                                   @Param("radius") double radius);

}