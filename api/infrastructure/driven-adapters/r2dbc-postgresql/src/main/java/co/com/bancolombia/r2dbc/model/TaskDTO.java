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
