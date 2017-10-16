package com.kr4ken.habitica;

import com.kr4ken.habitica.domain.*;

import java.util.List;

public interface Habitica {
 List<Task> getUserTasks(Argument ...args);
}
