# FirstTeamProject

# Общие конвенции
- Реализация каждого модуля пишется в отдельной папке проекта.
- Реализация каждого подмодуля, если такие появляются при необходимости, пишется в отдельной папке модуля.
- Абстракции пишутся в отдельной папке __abstractions__
- В этом документе описываются API модулей, не их реализация.
- Помним, что один класс может реализовать несколько интерфейсов. Так же помним про остальные принципы SOLID.
- Помним так же и про принцип Dependency Invertion.
- Каждый функциональный блок должен осуществлять логирование через соответствующий интерфейс. 

Если возникает необходимость в "затычке" абстракции, которая на данный момент не реализована, в гитигнор добавлена папка `/project/src/main/java/test/`. Для прочих файлов `/project/test/`. 

# Архитектура

Более уместным и простым будет использовать архитектуру Model View Controller.
На пальцах:
- Controller считывает и заставляет считать Model
- Model считает и заставляет показывать View
- View показывает

Соответственно мы имеем вот такую цепочку зависимостей:
```mermaid
graph LR
  A -- Ввод данных --> B(Controller)
  B -- Передача сырых считанных данных --> C(Model)
  C -- Передача обработанных данных --> D(View)
  B -- Уведомляет об ошибках --> D
  D -- Показывает данные --> A{Живой пользователь}

```

В ТЗ не уточнено, обязательно-ли использовать консоль или можно использовать UI фреймворк. 

Если UI фреймворк использоваться не будет, то View будут отвечать за форматированное красивое отображение данных в консоли и запись в файлы. Controller сооветственно за цикличный ввод данных и соответственно завершение программы.

Если решимся использовать UI фреймворк, то надо будет его адаптировать под архитектуру.


## View
> В данном модуле осуществляется отображение информации пользователю. 
Классы модуля:
```mermaid
classDiagram

  note for ErrorPresenter "Предоставить текст ошибки пользователю"
  note for CollectionPresenter "Предоставить пользователю отсортированную коллекцию
  Для доп задания 2 необходимо записывать результат в файл."
  note for MatchesPresenter "Предоставить количество совпадений 
  пользователю по доп заданию 4"

  class ErrorPresenter {
    +sendError(String header, String messageText)
  }

  class CollectionPresenter {
    +sendCollection(EntityCollection collection)
  }

  class MatchesPresenter {
    +sendMatches(int matches)
  }
```

## Controller
> В данном модуле осуществляется ввод и валидация данных. 
Будь то ввод с клавиатуры, чтение файла и т.д.
Данный модуль отвечает за целостность данных, которые попадают в модель.

### FileReader
> В данном модуле осуществляется чтение данных из файлов.

__Эндпоинты данного подмодуля:__
- FileReader

__FileReader__ 
Построчно читает файл. В зависимости от типа данных,
которые нужно прочитать, он использует соответствующий EntityRowReader.

__EntityRowReader__
Каждая реализация должна конвертировать строку в экземпляр класса через соответсвующий билдер.

Классы подмлуля:
```mermaid
classDiagram
  EntityRowReader~T extends Entity~ <|.. UserReader
  EntityRowReader~T extends Entity~ <|.. BusReader
  EntityRowReader~T extends Entity~ <|.. CarReader
  FileReader~T extends Entity~ --* EntityRowReader~T extends Entity~

  class FileReader~T extends Entity~{
    +EntityRowReader~T~ rowReader
    
    EntityCollection read(String path)
  }

  class EntityRowReader~T extends Entity~{
    <<interface>>
    T readRow(String row)
  }

  class UserReader{
    +UserBuilder builder

    User readRow(String row)
  }

  class BusReader{
    +BusBuilder builder

    Bus readRow(String row)
  }

  class CarReader{
    +CarBuilder builder

    Car readRow(String row)
  }

```

## Model
> В данном модуле осуществляется взаимодействие с данными.
Основная логика, которая должна выполняться эффективно.

__Эндпоинты данного модуля:__
- класс SorterBuilder
- класс EntitySorter
- класс EntityFinder

__Инициализация наследника EntitySorter должна проводиться только через билдер.__
Билдер необходим, так как алгоритм сортировки должен быть один, а способов сравнения 2.
Задача билдера - подставить нужный EntityComporator в EntitySorter.

__EntitiFinder__
Должен найти все повторения Entity в коллекции. Использует исключительно DefaultComporator.

__DefaultComporator__
Он просто вызывает compareTo() у Entity.

__AlternativeComporator__
Должен выполнять условия доп задания 1.

По окончанию сортировки

Классы модуля:
```mermaid
classDiagram

  EntityComporator <|.. DefaultComporator
  EntityComporator <|.. AlternativeComporator
  EntityFinder <|.. DefaultEntityFinder
  EntitySorter *-- EntityComporator
  DefaultEntityFinder *-- DefaultComporator
  SorterBuilder --> EntityComporator
  SorterBuilder --> EntitySorter
  EntitySorter --> XXXSorter

  class EntityFinder{
    <<abstract>>

    int findEntity(EntityCollection collection, Entity target)
  }

  class DefaultEntityFinder{
    <<abstract>>
    + DefaultEntityComporator comporator

    int findEntity(EntityCollection collection, Entity target)
  }

  class EntitySorter{
    <<abstract>>
    +EntityComporator сomporator;
    +CollectionPresenter presenter;

    EntityCollection sortEntities(EntityCollection collection)
  }


  note for XXXSorter "Конкретная реализация алгоритма сортировки.
  Название зависит от выбранного алгоритма."
  class XXXSorter{
    EntityCollection sortEntities(EntityCollection collection)
  }

  class SorterBuilder{
    setDefaultComporator()
    setAlternativeComporatot()
    EntitySorter build()
  }

  class EntityComporator{
    <<interface>>
    int compare(Entity first, Entity second)
  }
  class DefaultComporator{
    int compare(Entity first, Entity second)
  }
  class AlternativeComporator{
    int compare(Entity first, Entity second)
  }
```

## Shared
> В этом модуле представлены объекты, использующиеся повсеместно во всех модулях.

### Entities
> В этом подмодуле представлены сущности приложения.

Экземпляры Entity должны быть инициализированы исключительно через соответствующий Builder.
Абстрактный класс Entity должен реализовать Comparable\<Entity>. В нем сравнивается название типа.
Каждый наследник Entity должен переопределить реализацию Comparable\<Entity>. В нем вызвать базовую реализацию метода compareTo, в случае если она вернула 0, произвести сравнение по всем полям.

Этот модуль должен содержать классы:
```mermaid
---
title: Исходя из общих требований, 
  для каждого наследника Entity необходимо реализовать
  сравнение по всем полям. 
  Сортировать стоит в порядке, указанном на этой диаграме.
---
classDiagram

  Entity <|.. Bus
  Entity <|.. User
  Entity <|.. Car

  Bus --> BusBuilder
  User --> UserBuilder
  Car --> CarBuilder
  
  EntityCollection --> EntityCollectionFactory

  class Entity {
    <<abstarct>>
    int compareTo(Entity other)
  }

  class EntityCollection{
    Iterable~Entity~ getEntities()
    List~Entity~ getEntitiesList()
  }

  class EntityCollectionFactory{
    EntityCollection createEntityCollection(List<Entity> entities);
  }

  class EntityCollectionFactory

  class Bus {
    +int number
    +String model
    +int mileage

    int getNumber()
    String getModel()
    int getMilage()
  }

  class BusBuilder{
    setNumber(int number)
    setModel(string model)
    setMileage(int mileage)
    Bus build()
  }

  class User {
    +String name
    +String password
    +String email
    +int age

    String getName()
    String getpassword()
    String getEmail()
    int getAge()
  }

  class UserBuilder{
    setName(String name)
    setPassword(String password)
    setEmail(String Email)
    setAge(int age)
    User build()
  }

  class Car{
    +int enginePower
    +String model
    +LocalDate releaseDate

    int getEnginePower()
    String getModel()
    LocalDate getReleaseDate()
  }

  class CarBuilder{
    setEnginePower(int enginePower)
    setModel(string model)
    setReleaseDate(int releaseDate)
    Car build()
  }
```

### Logging
> Этот подмодуль отвечает за логирование приложения.

__Каждый модуль с помощью DI будет получать соответствующий ModuleLoggerDecorator__
Задача декоратора - добавить к логу, которую он получает от модуля, название модуля.

Эта спорная часть архитектуры. Требуется обсуждение с исполнителем.

Классы подмодуля:
```mermaid
classDiagram

  Logger <|.. ModuleLoggerDecorator
  Logger <|.. FileLogger

  ModuleLoggerDecorator --> FileLogger

  Logger --> Level
  FileLogger --> Level
  ModuleLoggerDecorator --> Level

  class Logger{
    <<interface>>
    log(Level level, String log)
  }

  class Level{

    String toString()
  }

  class ModuleLoggerDecorator{
    +Logger logger
    +String modulaName

    log(Level level, String log)
  }

  class FileLogger{
    log(Level level, String log)
  }

```
Каждый лог должен быть в следующем формате:
`[Уровень серьезности][Модуль] Сообщение`
Например:
```
[Info][Model] Сортировка выполнена
[Fatal][View] Выброшено необработаное исключение
[Warning][Controller] Данные введены неправильно
```

## Tests
> В данном модуле будет содержаться покрытие кода тестами.

К этому модулю будем приступать в последнюю очередь. В первую очередь нужно будет покрыть тестами сортировку. Ну и возможно валидацию контроллера.
