package com.consist.task.model;

import com.consist.task.model.entity.TaskEntity;
import com.consist.task.model.entity.TaskParameter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskUtils {

    private TaskUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Обходит дерево подзадач и возвращает все задачи в виде списка.
     * Корневая задача также входит в список.
     * Модифицирует подзадачи, добавляя ID родительской задачи
     *
     * @param task Корневая задача, с которой начинается обход
     * @return Список всех подзадач для переданного корня
     */
    public static List<TaskEntity> taskTree2List(TaskEntity task) {
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
    public static List<TaskParameter> paramTree2List(TaskEntity task) {
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
    public static TaskEntity taskList2Tree(List<TaskEntity> allTasks, List<TaskParameter> allParams, TaskEntity root) {
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
     * Проходит по переданным множествам и сравнивает задачи по id.
     * Если id совпали, добавляет две совпавшие задачи во второй возвращаемый список.
     * Если задачи с таким id из первого множества нет во втором, то добавляется в первый возвращаемый список
     *
     * @param lhs Множество из которого проверяются задачи во втором множестве
     * @param rhs Множество, содержащее проверяемые задачи.
     * @return Задачи из первого множества, которых нет во втором.
     */
    public static Pair<List<TaskEntity>, List<Pair<TaskEntity, TaskEntity>>> getIntersectionById(List<TaskEntity> lhs, List<TaskEntity> rhs) {
        List<TaskEntity> nonintersections = new ArrayList<>();
        List<Pair<TaskEntity, TaskEntity>> intersections = new ArrayList<>();

        for (TaskEntity lhsSubtask : lhs) {
            boolean flag = true;
            for (TaskEntity rhsSubtask : rhs)
                if (lhsSubtask.getId().equals(rhsSubtask.getId())) {
                    flag = false;
                    intersections.add(new ImmutablePair<>(lhsSubtask, rhsSubtask));
                    break;
                }
            if (flag) {
                nonintersections.add(lhsSubtask);
            }
        }
        return new ImmutablePair<>(nonintersections, intersections);
    }
}
