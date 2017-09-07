package com.kr4ken.dp.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    Collection<Interest> findByAccountUsername(String username);
}
