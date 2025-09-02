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
