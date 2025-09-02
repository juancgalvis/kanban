package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.model.TaskDTO;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// TODO: This file is just an example, you should delete or modify it
public interface MyReactiveRepository extends ReactiveCrudRepository<TaskDTO, String>, ReactiveQueryByExampleExecutor<TaskDTO> {

}
