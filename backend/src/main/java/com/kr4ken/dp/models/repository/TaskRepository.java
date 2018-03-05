package com.kr4ken.dp.models.repository;

import com.kr4ken.dp.models.entity.Task;
import com.kr4ken.dp.models.entity.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long> {
    Optional<Task> findByName(String name);
    Optional<Task> findByTrelloId(String trelloId);
    Collection<Task> findByType(TaskType type);
}
