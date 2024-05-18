package com.sharesapp.backend.repository;

import com.sharesapp.backend.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
  @Query("SELECT u FROM User u JOIN u.shares s WHERE s.company.id = :companyId AND "
      + "s.lastSalePrice BETWEEN :minPrice AND :maxPrice")
  List<User> findUsersByCompanyAndSharePriceRange(@Param("companyId") Long companyId,
                                                  @Param("minPrice") Float minPrice,
                                                  @Param("maxPrice") Float maxPrice);
}

