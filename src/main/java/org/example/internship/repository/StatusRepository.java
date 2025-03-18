package org.example.internship.repository;

import org.example.internship.entity.StatusEntity;
import org.example.internship.entity.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<StatusEntity, Long> {
    List<StatusEntity> findAllByType(StatusType type);
}
