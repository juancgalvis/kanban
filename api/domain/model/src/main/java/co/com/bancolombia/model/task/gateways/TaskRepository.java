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
