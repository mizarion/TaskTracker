package com.consist.taskboot.component.separator;

import com.consist.taskboot.model.entity.TaskEntity;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TaskSeparatorImpl implements EntitySeparator<TaskEntity> {

    @Override
    public SeparateResult<TaskEntity> separate(List<TaskEntity> db, List<TaskEntity> update) {
        return new SeparateResult<>() {
            @Override
            public List<TaskEntity> created() {
                return update.stream().filter(lhs -> db.stream()
                                .noneMatch(rhs -> lhs.getId().equals(rhs.getId())))
                        .collect(Collectors.toCollection(ArrayList::new));
            }

            @Override
            public Map.Entry<List<TaskEntity>, List<TaskEntity>> updated() {
                List<TaskEntity> inDb = new ArrayList<>();
                List<TaskEntity> inUpdate = new ArrayList<>();
                for (TaskEntity dbTask : db) {
                    for (TaskEntity updateTask : update)
                        if (dbTask.getId().equals(updateTask.getId())) {
                            inDb.add(dbTask);
                            inUpdate.add(updateTask);
                            break;
                        }
                }
                return new AbstractMap.SimpleEntry<>(inDb, inUpdate);
            }

            @Override
            public List<TaskEntity> deleted() {
                return db.stream().filter(lhs -> update.stream()
                                .noneMatch(rhs -> lhs.getId().equals(rhs.getId())))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        };
    }
}