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
