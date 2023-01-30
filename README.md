# Task

Проект на java 7 без использования Spring Boot

# Taskboot

Проект на java 17 с использованием Spring Boot, Testcontainers, flywaydb....

## Описание

Проект представляет CRUD операции над задачей.

### Задачи

Задача состоит из:

* _id_ - id задачи
* _name_ - имя задачи.
* _status_ - статус задачи.
* _parameters_ - список параметров для данной задачи.
* _subtasks_ - список подзадач.

### Подзадачи

_Подзадача_ - это _задача_, которая является вложенной сущностью другой задачи.
<br>При модификации родительской задачи могут изменяться вложенные сущности.
<br>При удалении задачи все ее вложенные сущности удаляются (каскадом).

Количество или уровень вложенности подзадач не ограничен.

## Пример запроса:

```json
{
  "id": 38,
  "status": "READY",
  "name": "task38",
  "parameters": [],
  "subtasks": []
}
```

## Пример запроса 2:

```json
{
  "id": 40,
  "status": "READY",
  "name": "task40",
  "parameters": [
    {
      "type": "int",
      "taskName": "taskparam",
      "value": "100"
    }
  ],
  "subtasks": [
    {
      "id": 41,
      "status": "READY",
      "name": "subtask41",
      "parameters": [],
      "subtasks": []
    }
  ]
}
```

# Тестирование

## Доступные профили

1. ```mvn test -P unit```  Для Unit-тестирования.
2. ```mvn verify -P it``` Для интеграционного тестирования.
3. ```mvn verify -P live ``` - Для Live тестирования (RestTemplate).

## Возможная передача параметров

### Параметры для Интеграционного тестирования

Возможно явно указать параметры для бд:

```        
-Durl=jdbc:h2:mem:consistdb;DB_CLOSE_DELAY=-1 -Dusername=username -Dpassword=password -Ddriver="org.h2.Driver"
```

Пример с H2:

```
mvn verify -P unit -P it  -Durl="jdbc:h2:mem:consistdb;DB_CLOSE_DELAY=-1" -Dusername=username -Dpassword=password -Ddriver="org.h2.Driver"
```

Пример с postgres:

```
mvn verify -P unit -P it  -Durl="jdbc:postgresql://localhost:5432/consistdb" -Dusername=postgres -Dpassword=12345QWe -Ddriver="org.postgresql.Driver"
```

### Параметры для Live тестирования

Возможно задать адрес (по умолчанию - ```http://localhost:8080/tasks```)

```
mvn verify -P live -Daddress="http://localhost:8080/task-0.0.1-SNAPSHOT/tasks"
```
