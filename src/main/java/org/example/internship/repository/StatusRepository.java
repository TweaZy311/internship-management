package org.example.internship.repository;

import org.example.internship.model.Status;
import org.example.internship.model.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    List<Status> findAllByNameContaining(String name);
    List<Status> findAllByTypeAndNameContaining(StatusType type, String name);
    Status findByTypeAndNameContaining(StatusType type, String name);
}
