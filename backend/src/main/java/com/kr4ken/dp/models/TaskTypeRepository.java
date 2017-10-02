package com.kr4ken.dp.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskTypeRepository extends JpaRepository<TaskType,Long> {
    Optional<TaskType> findByName(String name);
    Optional<TaskType> findByTrelloId(String trelloId);
}
