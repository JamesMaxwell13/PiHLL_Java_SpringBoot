package com.sharesapp.backend.repository;

import com.sharesapp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN u.shares s WHERE s.company.id = :companyId AND s.lastSalePrice BETWEEN :minPrice AND :maxPrice")
    List<User> findUsersByCompanyAndSharePriceRange(@Param("companyId") Long companyId,
                                                              @Param("minPrice") Float priceStart,
                                                              @Param("maxPrice") Float priceEnd);
}

