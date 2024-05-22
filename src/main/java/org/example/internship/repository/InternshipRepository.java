package org.example.internship.repository;

import org.example.internship.model.internship.Internship;
import org.example.internship.model.internship.InternshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы со стажировками.
 */
@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {

    /**
     * Поиск стажировок по статусу.
     *
     * @param status статус стажировки
     * @return список стажировок с указанным статусом
     */
    List<Internship> findByStatus(InternshipStatus status);
}
