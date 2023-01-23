package com.consist.taskboot.component.separator;

import java.util.List;
import java.util.Map;

public interface EntitySeparator<T> {

    interface SeparateResult<T> {
        List<T> created();

        /**
         * @return Ключ - элементы из первого множества, Значение - элементы из второго множества
         */
        Map.Entry<List<T>, List<T>> updated();

        List<T> deleted();
    }

    SeparateResult<T> separate(List<T> db, List<T> update);
}