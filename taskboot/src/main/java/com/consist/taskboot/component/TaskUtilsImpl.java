package com.consist.taskboot.component;

import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.model.entity.TaskParameter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
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
    public List<TaskEntity> taskTree2List(TaskEntity task) {
        List<TaskEntity> list = new ArrayList<>(Collections.singletonList(task));
        for (TaskEntity subtask : task.getSubTasks()) {
            subtask.setParent(task.getId());
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
    public List<TaskParameter> paramTree2List(TaskEntity task) {
        List<TaskParameter> list = new ArrayList<>();
        for (TaskParameter param : task.getTaskParameters()) {
            param.setTaskId(task.getId());
            list.add(param);
        }

        for (TaskEntity subtask : task.getSubTasks()) {
            subtask.setParent(task.getId());
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
    public TaskEntity taskList2Tree(List<TaskEntity> allTasks, List<TaskParameter> allParams, TaskEntity root) {
        List<TaskEntity> subtasks = new ArrayList<>();
        for (TaskEntity subtask : allTasks) {
            if (subtask.getParentId().equals(root.getId())) {
                subtasks.add(taskList2Tree(allTasks, allParams, subtask));
            }
        }
        List<TaskParameter> parameters = new ArrayList<>();
        for (TaskParameter param : allParams) {
            if (param.getTaskId().equals(root.getId())) {
                parameters.add(param);
            }
        }
        return new TaskEntity(root.getId(), root.getStatus(), root.getTaskName(), parameters, subtasks);
    }

    /**
     * Находит в переданных множествах НЕ пересечения по id.
     *
     * @param lhs Множество из которого проверяются задачи во втором множестве
     * @param rhs Множество, содержащее проверяемые задачи.
     * @return Задачи из первого множества, которых нет во втором.
     */
    public List<TaskEntity> getNonIntersectionById(List<TaskEntity> lhs, List<TaskEntity> rhs) {
        List<TaskEntity> nonintersections = new ArrayList<>();
        for (TaskEntity lhsSubtask : lhs) {
            boolean flag = true;
            for (TaskEntity rhsSubtask : rhs)
                if (lhsSubtask.getId().equals(rhsSubtask.getId())) {
                    flag = false;
                    break;
                }
            if (flag) {
                nonintersections.add(lhsSubtask);
            }
        }
        return nonintersections;
    }
}
