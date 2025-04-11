package org.example.internship.repository;

import org.example.internship.entity.InternshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы со стажировками.
 */
@Repository
public interface InternshipRepository extends PagingAndSortingRepository<InternshipEntity, Long>, JpaSpecificationExecutor<InternshipEntity>, JpaRepository<InternshipEntity, Long> {

    /**
     * Поиск стажировок по статусу.
     *
     * @param statusId ID статуса стажировки
     * @return список стажировок с указанным статусом
     */
    List<InternshipEntity> findByStatusId(Long statusId);

    List<InternshipEntity> findByIsOpen(Boolean isOpen);
}
