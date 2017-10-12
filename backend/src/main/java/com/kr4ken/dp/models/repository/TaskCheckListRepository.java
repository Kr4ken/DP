package com.kr4ken.dp.models.repository;

import com.kr4ken.dp.models.entity.TaskCheckList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskCheckListRepository extends JpaRepository<TaskCheckList,Long> {
    Optional<TaskCheckList> findByName(String name);
    Optional<TaskCheckList> findByTrelloId(String trelloId);
}
