package org.example.internship.repository;

import org.example.internship.entity.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends PagingAndSortingRepository<AuditEntity, Long>, JpaSpecificationExecutor<AuditEntity>, JpaRepository<AuditEntity, Long> {

}
