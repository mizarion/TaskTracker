package com.consist.taskboot.component;

import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.model.entity.TaskParameter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskUtilsImpl implements TaskUtils {

    /**
     * Обходит дерево подзадач и возвращает все задачи в виде списка.
     * Корневая задача также входит в список.
     * Модифицирует подзадачи, добавляя ID родительской задачи
     *
     * @param task Корневая задача, с которой начинается обход
     * @return Список всех подзадач для переданного корня
     */
    @Override
    public List<TaskEntity> taskTree2List(TaskEntity task) {
        List<TaskEntity> list = new ArrayList<>(List.of(task));
        for (TaskEntity subtask : task.getSubTasks()) {
            list.addAll(taskTree2List(subtask));
        }
        return list;
    }

    /**
     * Обходит дерево подзадач и возвращает все параметры задач в виде списка.
     * Параметры корневая задачи также входят в список.
     * Модифицирует параметры, добавляя ID задачи
     *
     * @param task Корневая задача, с которой начинается обход
     * @return Список всех параметров для переданного корня
     */
    @Override
    public List<TaskParameter> paramTree2List(TaskEntity task) {
        List<TaskParameter> list = new ArrayList<>(task.getTaskParameters());
        for (TaskEntity subtask : task.getSubTasks()) {
            list.addAll(paramTree2List(subtask));
        }
        return list;
    }

    /**
     * Строит дерево задач из переданного списка относительно указанного корня.
     *
     * @param allTasks  Список всех задач
     * @param allParams Список всех параметров.
     * @param root      Задача, которая является корнем для данной итерации
     * @return Задача, указанная в качестве корня с присвоенным деревом подзадач из списка и параметрами
     */
    @Override
    public TaskEntity taskList2Tree(List<TaskEntity> allTasks, List<TaskParameter> allParams, TaskEntity root) {
        List<TaskEntity> subtasks = new ArrayList<>();
        for (TaskEntity subtask : allTasks) {
            if (subtask.getParentId().equals(root.getId())) {
                subtasks.add(taskList2Tree(allTasks, allParams, subtask));
            }
        }
        List<TaskParameter> parameters = new ArrayList<>();
        for (TaskParameter param : allParams) {
            if (param.taskId().equals(root.getId())) {
                parameters.add(param);
            }
        }
        return new TaskEntity(root.getId(), root.getStatus(), root.getTaskName(), parameters, subtasks);
    }

}
