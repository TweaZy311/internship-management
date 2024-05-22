package org.example.internship.repository;

import org.example.internship.model.application.Application;
import org.example.internship.model.application.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с заявками на стажировку.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    /**
     * Поиск заявки по номеру телефона человека и идентификатору стажировки.
     *
     * @param phoneNumber  номер телефона, связанный с заявкой
     * @param internshipId идентификатор стажировки
     * @return найденная заявка или null, если заявка не найдена
     */
    Application findByPhoneNumberAndInternshipId(String phoneNumber, Long internshipId);

    /**
     * Поиск заявок по статусу.
     *
     * @param status статус заявки
     * @return список заявок с указанным статусом
     */
    List<Application> findAllByStatus(ApplicationStatus status);

    /**
     * Поиск заявок по идентификатору стажировки.
     *
     * @param internshipId идентификатор стажировки
     * @return список заявок оставленных на указанную стажировку
     */
    List<Application> findAllByInternshipId(Long internshipId);

    /**
     * Поиск заявок по статусу и идентификатору стажировки.
     *
     * @param internshipId идентификатор стажировки
     * @param status       статус заявки
     * @return список заявок с указанным статусом и оставленных на указанную стажировку
     */
    List<Application> findAllByInternshipIdAndStatus(Long internshipId, ApplicationStatus status);
}
