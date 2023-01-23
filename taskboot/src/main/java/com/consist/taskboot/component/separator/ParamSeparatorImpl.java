package com.consist.taskboot.component.separator;

import com.consist.taskboot.model.entity.TaskParameter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ParamSeparatorImpl implements EntitySeparator<TaskParameter> {

    @Override
    public SeparateResult<TaskParameter> separate(List<TaskParameter> db, List<TaskParameter> update) {
        return new SeparateResult<>() {
            @Override
            public List<TaskParameter> created() {
                return update.stream()
                        .filter(newParam -> !db.contains(newParam))
                        .collect(Collectors.toCollection(ArrayList::new));
            }

            /**
             * Ничего не возвращает т.к. измененные параметры становятся новыми
             *
             * @return null
             */
            @Override
            public Map.Entry<List<TaskParameter>, List<TaskParameter>> updated() {
                return null;
            }

            @Override
            public List<TaskParameter> deleted() {
                return db.stream()
                        .filter(dbParam -> !update.contains(dbParam))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        };
    }
}