#Divine providence

Общий префикс
`/api/v1/`

Для большинства запросов можно указать параметр `trello` чтобы синхронизировать действие с Трелло

InterestType
---
Различные типы интересов

```json
{
  "id":1,
  "name":"Наименование типа интереса",
  "description":"Описание типа интереса",
  "trelloId":"Id соответствующего листа в трелло"
}
```

| Метод | Путь | Описание |
| ----  | ---- | ----  |
| GET  | /interestTypes/ | Получить список всех типов интересов  |
| GET  | /interestTypes/{id} | Получить данные типа интереса с id  |
| POST  | /interestTypes/ | Создать новый тип интереса  |
| PUT  | /interestTypes/{id} | Обновить тип интереса с id |
| DELETE  | /interestTypes/{id} | Удалить тип интереса с id  |



Interests
---
Интересы 

```json
{
    "name": "Наименование интереса",
    "img":"URL картинки",
    "source":"URL с источником для интереса",
    "season":0 ,
    "stage":0,
    "type": {
        "id":0 },                    
    "ord":1.444,
    "description":"Описание интереса",
    "trelloId": "Id карточки в трелло"
}
```
В GET запросе можно добавить параметры `type={id}&ord={pos}` для выборки интересов с типом интереса id и pos позиции в своем типе интереса

| Метод | Путь | Описание |
| ----  | ---- | ----  |
| GET | /interests/ | Список интересов в системе |
| GET | /interests/current | Текущие интересы по каждому типу |
| GET | /interests/{id} | Выводит информацию о конкретном интересе |
| POST | /interests/ | Добавляет новый интерес в список |
| PUT | /interests/{id} | Обновляет информацию о конкретном интересе |
| DELETE | /interests/{id} | Удаляет конкретный интерес |

Trello
---
Это не объекты, а операции которые выполняются с трелло

| Метод | Путь | Описание |
| ----  | ---- | ----  |
|*Импорт*||
| POST | /trello/import | Импорт типов интересов и интересов из Трелло |
| POST | /trello/import/interestTypes | Импорт типов интересов из Трелло |
| POST | /trello/import/interestTypes/{id} | Обновление значений типа интереса из Трелло |
| POST | /trello/import/interests | Импорт интересов из Трелло |
| POST | /trello/import/interests/{id} | Обновление интереса из Трелло |
|*Экспорт*||
| POST | /trello/export | Экспорт типов интересов и интересов в Трелло |
| POST | /trello/export/interestTypes | Экспорт типов интересов в Трелло |
| POST | /trello/export/interestTypes/{id} | Обновление значений типа интереса в Трелло |
| POST | /trello/export/interests | Экспорт интересов в Трелло |
| POST | /trello/export/interests/{id} | Обновление интереса в Трелло |

Divine
---
Это не объекты, а операции которые предоставляет система

| Метод | Путь | Описание |
| ----  | ---- | ----  |
|*Логика*||
| POST | /divine/mix/{id} | Выбор нового случайного интереса для конкретного типа (id)|
| POST | /divine/complete/{id} | Перенос текущего интереса в список _*Выполнено*_ и выбор нового случайного интереса для конкретного типа (id)|
| POST | /divine/refer/{id} | Перенос текущего интереса в список _*Отложено*_ и выбор нового случайного интереса для конкретного типа (id)|
| POST | /divine/drop/{id} | Удаление текущего интереса и выбор нового случайного интереса для конкретного типа (id)|

---
Task
---


#Trello Card <-> DivineTask

```json
//Запрос
// /cards/:id
{
  "id": Task.trelloId,
  "closed": Task.type == Closed.type,
  "dueComplete": Task.dueDate > Date.today && Task.Type == Closed.type,
  "desc": Task.description"[special]("Task.special")",
  "due": Task.dueDate,
  "idChecklists": [
    // доп запрос 
    // /cards/:id/checklists
      {
        "id": Task.checklists[n].trelloId,
        "name": Task.checklists[n].name
        "checkItems": [
          {
            "state": Task.checklists[n].checklistItems[k].checked ?"complete":"incomplete",
            "id": Task.checklists[n].checklistItems[k].trelloId,
            "name": Task.checklists[n].checklistItems[k].name"["Task.checklists[n].checklistItems[k].duration"]",
            "pos": Task.checklists[n].checklistItems[k].pos
          }]
    }
  ],
  "idList": Task.type.trelloId,
  "idAttachmentCover":{
    // доп запрос 
    // /cards/:id/attachments/:idAttachmentCover
    "id": "59e6f59e46812311f8783a82",
    "url": Task.img

  },
   "idLabels": [
    Task.important? idLabels.consist(IMPORTANT_LABEL):idLabels.consist(NOT_IMPORTANT_LABEL)
    Task.urgent? idLabels.consist(URGENT_LABEL):idLabels.consist(NOT_URGENT_LABEL)
    Task.Attribute? idLabels.consist(ATTRIBUTE_LABEL)
  ],
  //Чтобы в качестве обложки выбирался тот аттачмент который нужен
  "manualCoverAttachment": true,
  "name":Task.name + "["Task.duration"]"
}
}
```

#Habitica Task <-> DivineTask

============================================================
DAILY
============================================================
```json
{
            "userId": "userid",
            "alias": Task.trelloId,
            "text":"{"task.duration"}" " task.checklist[0].{firstNotCompleteItem}?task.checklist[0].{firstNotCompleteItem}" ["Task.name"]":Task.Name,
            "attribute": Task.attribute,
            "priority": 1,
            "value": 0,
            "tags": [
              Task.Type? TYPE_TAG
              Task.Urgent? URGENT_TAG
              Task.Important? IMPORTANT_TAG
            ],
            "notes": task.checklist? "!["Task.description"(http://progressed.io/bar/task.checklist[0].{completeCount}?scale=task.checklist[0].{count}&suffix=+)":Task.description
            "type": Task.type.Daily,
            "completed": false,
            "repeat": {
                "su": true,
                "s": false,
                "f": false,
                "th": true,
                "w": true,
                "t": true,
                "m": true
            },
            "id": "597b4880-041c-4774-bf8b-9d75c7263ded"
}
```
============================================================
HABIT
============================================================
```json
{
            "userId": "userId",
            "alias": Task.trelloId,
            "text":"{"task.duration"}" " task.checklist[0].{firstNotCompleteItem}?task.checklist[0].{firstNotCompleteItem}" ["Task.name"]":Task.Name,
            "attribute": Task.attribute,
            "priority": 1,
            "value": 0,
            "tags": [
              Task.Type? TYPE_TAG
              Task.Urgent? URGENT_TAG
              Task.Important? IMPORTANT_TAG
            ],
            "notes": task.checklist? "!["Task.description"(http://progressed.io/bar/task.checklist[0].{completeCount}?scale=task.checklist[0].{count}&suffix=+)":Task.description
            "type": Task.Type.Habit,
            "frequency": "daily",
            "id": "a08e462f-f780-4b17-8b0a-bededd41efc3"
}
```
============================================================
TODO
============================================================
```json
{
            "userId": "userId",
            "alias": Task.trelloId,
            "text":"{"task.duration"}" " task.checklist[0].{firstNotCompleteItem}?task.checklist[0].{firstNotCompleteItem}" ["Task.name"]":Task.Name,
            "date": Task.dueDate,
            "attribute": Task.attribute,
            "priority": 1,
            "value": 0,
            "tags": [
              Task.Type? TYPE_TAG
              Task.Urgent? URGENT_TAG
              Task.Important? IMPORTANT_TAG
            ],
            "notes": task.checklist? "!["Task.description"(http://progressed.io/bar/task.checklist[0].{completeCount}?scale=task.checklist[0].{count}&suffix=+)":Task.description
            "type": Task.Type.Todo,
            "completed": false,
            "id": "62be05e6-c68c-486c-9624-cb8ca8fd6f3d"
}
```