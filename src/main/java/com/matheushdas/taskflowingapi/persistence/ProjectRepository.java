package com.matheushdas.taskflowingapi.persistence;

import com.matheushdas.taskflowingapi.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Modifying
    @Query("UPDATE Project p SET p.status = ?2 WHERE p.id = ?1")
    Integer changeStateByProjectId(UUID id, String status);
}
