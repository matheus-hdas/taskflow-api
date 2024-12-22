package com.matheushdas.taskflowingapi.persistence;

import com.matheushdas.taskflowingapi.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Modifying
    @Query("UPDATE Task t SET t.status = ?2 WHERE t.id = ?1")
    Integer changeStateByTaskId(UUID id, String status);
}
