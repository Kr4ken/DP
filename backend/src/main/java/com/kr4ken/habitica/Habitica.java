package com.kr4ken.habitica;

import com.kr4ken.habitica.domain.*;

import java.util.List;

public interface Habitica {
 /**
  *  Tasks
  */
 List<Task> getUserTasks(Argument ...args);
 Task getTask(String taskId,Argument ...args);
 Task createTask(Task task);
 Task updateTask(Task task);
 void deleteTask(String taskId);


 /**
  * Tags
  */
 List<Tag> getUserTags();
 Tag getTag(String tagId);
 Tag createTag(Tag tag);
 Tag updateTag(Tag tag);
 void deleteTag(String tagId);

}
