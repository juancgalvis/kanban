# Proyecto de Ejemplo de uso de Plugin de Scaffold Clean Arquitecture

<img width="1066" height="509" alt="image" src="https://github.com/user-attachments/assets/c7fc49cc-4f6b-438b-9453-1511dc671df2" />


## Aplicación de Ejemplo

Se ha definido como escenario de ejemplo un tablero kanban básico con tareas, las cuales pueden ser creadas en estado `TODO`, las cuales pueden ser movidas a estado `IN_PROGRESS` y finalmente a estado `DONE`.

Se ha establecido que tendrá una persistencia en `postgres` y su lógica de negocio estará expuesta en mediante una api `REST`.

## Pasos Utilizados

### Creación de Estructura de proyecto con el plugin

```bash
mkdir api
```

Creación de archivo `build.gradle`

`/api/build.gradle`
```gradle
plugins {
    id 'co.com.bancolombia.cleanArchitecture' version '3.25.0'
}
```

Generación de estructura
```bash
gradle ca --metrics false
```

### Generación de Objetos de Dominio

#### Generación de Modelo

```bash
gradle gm --name Task
```

Se definen las propiedades del modelo Task...
Propiedades de la clase `Task`
```java
package co.com.bancolombia.model.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Task {
    private String id;
    private String title;
    private String description;
    private StatusEnum status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @AllArgsConstructor
    public enum StatusEnum {
        TODO,
        IN_PROGRESS,
        DONE;
    }
}
```

Se definen los métodos del puerto o adaptador para la persistencia del modelo Task...

```java
package co.com.bancolombia.model.task.gateways;

import co.com.bancolombia.model.task.Task;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskRepository {
    Mono<Task> createTask(Task task);

    Mono<Task> updateTask(Task task);

    Mono<Task> getById(String id);

    Mono<Void> deleteById(String id);

    Flux<Task> getAll();
}
```

#### Generación de Caso de Uso

(podrían ser varios casos de uso idependientes o podría ser uno o varios con varias operaciones dependiendo del escenario)

En este caso vamos a tener un caso de uso llamado `TaskManagement`

```bash
gradle guc --name TaskManagement
```

Se definen los métodos de la lógica de negocio y allí van las reglas o controles de negocio que se deben implementar:

```java
package co.com.bancolombia.usecase.taskmanagement;

import co.com.bancolombia.model.task.Task;
import co.com.bancolombia.model.task.gateways.TaskRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TaskManagementUseCase {
    private final TaskRepository repository;

    public Mono<Task> createTask(Task task) {
        // Do some business logic if needed
        return repository.createTask(task);
    }

    public Mono<Task> updateTask(Task task) {
        // Do some business logic if needed
        return repository.updateTask(task);
    }

    public Mono<Task> getTaskById(String id) {
        // Do some business logic if needed
        return repository.getById(id);
    }

    public Mono<Void> deleteTaskById(String id) {
        // Do some business logic if needed
        return repository.deleteById(id);
    }

    public Flux<Task> getAllTasks() {
        // Do some business logic if needed
        return repository.getAll();
    }
}
```

### Generación de Adaptadores de Infraestructura

#### Generación de Driven Adapter de Persistencia En Memoria

Vamos a generar un adaptador de persistencia en memoria el cual deberá implementar el gateway o puerto definido en el dominio.

```bash
gradle gda --type generic --name inmemory-database
```

Creamos la clase dentro de ese modulo y paquete
```java
package co.com.bancolombia.inmemorydatabase;

import co.com.bancolombia.model.task.Task;
import co.com.bancolombia.model.task.gateways.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@AllArgsConstructor
public class InMemoryTaskRepository implements TaskRepository {
    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    @Override
    public Mono<Task> createTask(Task task) {
        return Mono.fromSupplier(() -> {
            task.setId(UUID.randomUUID().toString());
            var now = OffsetDateTime.now();
            if(task.getCreatedAt() == null) {
                task.setCreatedAt(now);
            }
            task.setUpdatedAt(now);
            tasks.add(task);
            return task;
        });
    }

    @Override
    public Mono<Task> updateTask(Task task) {
        return deleteById(task.getId())
                .then(createTask(task));
    }

    @Override
    public Mono<Task> getById(String id) {
        return Mono.justOrEmpty(tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst());
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromRunnable(() -> tasks.removeIf(task -> task.getId().equals(id)));
    }

    @Override
    public Flux<Task> getAll() {
        return Flux.fromIterable(tasks);
    }
}
```

#### Generación de Entry Point de exposición via REST

En este caso vamos a usar un swagger con la definición de la API para acelerar un poco mas el ejemplo

```bash
gradle gep --type webflux --router false --from-swagger ../swagger.yaml
```

Esto genera también modelos que son definidos en la capa de EntryPoint, en este caso podríamos utilizar directamente los del dominio, en caso de que se requiera usar anotaciones propias del EntryPoint lo que se hace es un mapeo de objetos de EntryPoint al modelo de dominio, pero nunca se le pondrán detalles técnicos a los modelos del Dominio.

Se va a usar el mismo modelo del dominio, entonces se elimina el modelo `Task` creado en el entrypoint y se apunta al modelo `Task` que se encuentra en el dominio.

```java
package co.com.bancolombia.api;

import co.com.bancolombia.model.task.Task;
import co.com.bancolombia.usecase.taskmanagement.TaskManagementUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Log4j2
@AllArgsConstructor
@RestController
public class DefaultApiController {
    private final TaskManagementUseCase useCase;

    @GetMapping(value = "/tasks")
    public Mono<ResponseEntity<List<Task>>> tasksGet() {
        return useCase.getAllTasks()
                .collectList()
                .map(response -> ResponseEntity.ok().body(response));
    }

    @PostMapping(value = "/tasks",
            consumes = {"application/json"})
    public Mono<ResponseEntity<Task>> tasksPost(
            @RequestBody() Task body) {
        return useCase.createTask(body)
                .map(response -> ResponseEntity.ok().body(response));
    }

    @DeleteMapping(value = "/tasks/{taskId}")
    public Mono<ResponseEntity<Void>> tasksTaskIdDelete(@PathVariable("taskId") String taskId
    ) {
        return useCase.deleteTaskById(taskId)
                .then(Mono.fromSupplier(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping(value = "/tasks/{taskId}",
            produces = {"application/json"})
    public Mono<ResponseEntity<Task>> tasksTaskIdGet(@PathVariable("taskId") String taskId
    ) {
        return useCase.getTaskById(taskId)
                .map(response -> ResponseEntity.ok().body(response));
    }

    @PutMapping(value = "/tasks/{taskId}",
            produces = {"application/json"},
            consumes = {"application/json"})
    public Mono<ResponseEntity<Task>> tasksTaskIdPut(
            @RequestBody Task body, @PathVariable("taskId") String taskId
    ) {
        body.setId(taskId);
        return useCase.updateTask(body).map(response -> ResponseEntity.ok().body(response));
    }
}
```

| En este punto puede ejecutar la aplicación java

```bash
gradle bootRun
```

| La aplicación WEB

```bash
npm start
```

Acceder a [web](http://localhost:4200)

#### Generación de Driven Adapter de Persistencia En Postgres

Ejecutamos la tarea para generar el Driven Adapter

```bash
gradle gda --type r2dbc
```

Creamos la clase que servirá de DTO

```java
package co.com.bancolombia.r2dbc.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Data
@Table("tasks")
public class TaskDTO {
    @Id
    private String id;
    @Column
    private String title;
    @Column
    private String description;
    @Column
    private String status;
    @Column
    private OffsetDateTime createdAt;
    @Column
    private OffsetDateTime updatedAt;
}
```

Definimos los modelos en la interface Repository

```java
public interface MyReactiveRepository extends ReactiveCrudRepository<TaskDTO, String>, ReactiveQueryByExampleExecutor<TaskDTO> {

}
```

Implementamos el gateway de persistencia

```java
package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.task.Task;
import co.com.bancolombia.model.task.gateways.TaskRepository;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.model.TaskDTO;
import lombok.extern.log4j.Log4j2;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Log4j2
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Task,
        TaskDTO,
        String,
        MyReactiveRepository
        > implements TaskRepository {
    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Task.class/* change for domain model */));
    }

    @Override
    public Mono<Task> createTask(Task task) {
        return repository.save(toData(task)).map(this::toEntity);
    }

    @Override
    public Mono<Task> updateTask(Task task) {
        return createTask(task);
    }

    @Override
    public Mono<Task> getById(String id) {
        return repository.findById(id).map(this::toEntity);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<Task> getAll() {
        return repository.findAll().map(this::toEntity);
    }
}
```

Habilitamos las propiedades de configuración descomentando estas líneas

```java
 import org.springframework.boot.context.properties.ConfigurationProperties;

 @ConfigurationProperties(prefix = "adapters.r2dbc")

 ```

 Preparamos la Base de datos con el script `structure.sql`


 Luego en `application.yaml` colocamos las siguientes propiedades:

 ```yaml
 adapters:
  r2dbc:
    host: "localhost"
    port: 5432
    database: "postgres"
    username: "***"
    password: "***"
 ```

 Debe deshabilitar el adaptador en memoria, comentando la línea en el build.gradle de app-service

 ```gradle
//	implementation project(':inmemory-database')
 ```

 Finalmente puede ejecutar de nuevo la aplicación
