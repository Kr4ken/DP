package com.kr4ken.dp.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long> {
    Optional<Task> findByName(String name);
    Optional<Task> findByTrelloId(String trelloId);
}
