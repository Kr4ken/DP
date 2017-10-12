package com.kr4ken.dp.models.repository;

import com.kr4ken.dp.models.entity.TaskCheckListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskCheckListItemRepository extends JpaRepository<TaskCheckListItem,Long> {
    Optional<TaskCheckListItem> findByName(String name);
    Optional<TaskCheckListItem> findByTrelloId(String trelloId);
}
