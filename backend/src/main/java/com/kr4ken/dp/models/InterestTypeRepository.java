package com.kr4ken.dp.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestTypeRepository extends JpaRepository<InterestType, Long> {
    Optional<InterestType> findByName(String name);
}