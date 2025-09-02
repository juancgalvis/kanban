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
