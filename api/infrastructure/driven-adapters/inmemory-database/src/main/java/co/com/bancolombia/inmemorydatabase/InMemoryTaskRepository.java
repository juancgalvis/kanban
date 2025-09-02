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
