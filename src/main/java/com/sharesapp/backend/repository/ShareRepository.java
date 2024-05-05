package com.sharesapp.backend.repository;

import com.sharesapp.backend.model.Share;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareRepository extends JpaRepository<Share,Long> {
}
