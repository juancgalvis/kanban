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
