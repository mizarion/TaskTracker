package com.consist.taskboot.component;

import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.model.entity.TaskParameter;

import java.util.List;

public interface TaskUtils {

    /**
     * Обходит дерево подзадач и возвращает все задачи в виде списка.
     * Корневая задача также входит в список.
     * Модифицирует подзадачи, добавляя ID родительской задачи
     *
     * @param task Корневая задача, с которой начинается обход
     * @return Список всех подзадач для переданного корня
     */
    List<TaskEntity> taskTree2List(TaskEntity task);

    /**
     * Обходит дерево подзадач и возвращает все параметры задач в виде списка.
     * Параметры корневая задачи также входят в список.
     * Модифицирует параметры, добавляя ID задачи
     *
     * @param task Корневая задача, с которой начинается обход
     * @return Список всех параметров для переданного корня
     */
    List<TaskParameter> paramTree2List(TaskEntity task);

    /**
     * Строит дерево задач из переданного списка относительно указанного корня.
     *
     * @param allTasks  Список всех задач
     * @param allParams Список всех параметров.
     * @param root      Задача, которая является корнем для данной итерации
     * @return Задача, указанная в качестве корня с присвоенным деревом подзадач из списка и параметрами
     */
    TaskEntity taskList2Tree(List<TaskEntity> allTasks, List<TaskParameter> allParams, TaskEntity root);

    /**
     * Находит в переданных множествах НЕ пересечения по id.
     *
     * @param lhs Множество из которого проверяются задачи во втором множестве
     * @param rhs Множество, содержащее проверяемые задачи.
     * @return Задачи из первого множества, которых нет во втором.
     */
    List<TaskEntity> getNonIntersectionById(List<TaskEntity> lhs, List<TaskEntity> rhs);
}
