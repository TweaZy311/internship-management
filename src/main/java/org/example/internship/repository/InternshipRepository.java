package org.example.internship.repository;

import org.example.internship.entity.internship.InternshipEntity;
import org.example.internship.entity.internship.InternshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы со стажировками.
 */
@Repository
public interface InternshipRepository extends JpaRepository<InternshipEntity, Long> {

    /**
     * Поиск стажировок по статусу.
     *
     * @param statusId ID статуса стажировки
     * @return список стажировок с указанным статусом
     */
    List<InternshipEntity> findByStatusId(Long statusId);

    List<InternshipEntity> findByIsOpen(Boolean isOpen);
}
