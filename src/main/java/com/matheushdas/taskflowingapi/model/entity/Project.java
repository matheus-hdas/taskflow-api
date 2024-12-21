package com.matheushdas.taskflowingapi.model.entity;

import com.matheushdas.taskflowingapi.model.utility.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_project")
@Data
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Project(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public Project(UUID id, String name, String description) {
        this(name, description);
        this.id = id;
        this.status = Status.OPEN.getValue();
    }
}
