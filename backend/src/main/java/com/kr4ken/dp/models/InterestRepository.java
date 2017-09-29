package com.kr4ken.dp.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    Collection<Interest> findByName(String name);
    Collection<Interest> findByType(InterestType type);
    Optional<Interest> findByTrelloId(String trelloId);
    Collection<Interest> findByTypeOrderByOrd(InterestType interestType);
    Collection<Interest> findAllByOrderByOrd();
    Optional<Interest> findFirstByTypeOrderByOrd(InterestType interestType);
}

