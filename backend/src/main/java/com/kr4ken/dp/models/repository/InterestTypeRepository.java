package com.kr4ken.dp.models.repository;

import com.kr4ken.dp.models.entity.InterestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestTypeRepository extends JpaRepository<InterestType, Long> {
    Optional<InterestType> findByName(String name);
    Optional<InterestType> findByTrelloId(String trelloId);
}