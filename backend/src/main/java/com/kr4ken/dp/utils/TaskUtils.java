package com.kr4ken.dp.utils;

import com.kr4ken.dp.models.entity.Task;
import com.kr4ken.dp.models.entity.TaskCheckList;
import com.kr4ken.dp.models.entity.TaskCheckListItem;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskUtils {

    public static TaskCheckListItem getCurrentSubtask(Task task) {
        if (task.getChecklists() != null) {
            Optional<TaskCheckList> currentCheckList = task.getChecklists()
                    .stream()
                    .filter(e -> e.getChecklistItems() != null)
                    .filter(e -> e.getChecklistItems()
                            .stream()
                            .anyMatch(taskCheckListItem -> !taskCheckListItem.getChecked()))
                    .findAny();
            if (currentCheckList.isPresent()) {
                for (TaskCheckListItem item : currentCheckList.get().getChecklistItems().stream().sorted(Comparator.comparing(TaskCheckListItem::getPos)).collect(Collectors.toList())) {
                    if (!item.getChecked())
                        return item;
                }
            }
        }
        return null;
    }

    public static Long getCountSubtasks(Task task) {
        if (task.getChecklists() != null)
            return task.getChecklists()
                    .stream()
                    .flatMap(taskCheckList -> taskCheckList.getChecklistItems() != null ? taskCheckList.getChecklistItems().stream() : null)
                    .count();
        return 0L;
    }

    public static Long getCheckedCountSubtasks(Task task) {
        if (task.getChecklists() != null)
            return task.getChecklists()
                    .stream()
                    .flatMap(taskCheckList -> taskCheckList.getChecklistItems() != null ? taskCheckList.getChecklistItems().stream(): null)
                    .filter(TaskCheckListItem::getChecked)
                    .count();
        return 0L;
    }


}
