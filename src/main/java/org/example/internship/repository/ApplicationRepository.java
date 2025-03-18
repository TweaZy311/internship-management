package org.example.internship.repository;

import org.example.internship.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с заявками на стажировку.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
    /**
     * Поиск заявки по номеру телефона человека и идентификатору стажировки.
     *
     * @param phoneNumber  номер телефона, связанный с заявкой
     * @param internshipId идентификатор стажировки
     * @return найденная заявка или null, если заявка не найдена
     */
    ApplicationEntity findByPhoneNumberAndInternshipId(String phoneNumber, Long internshipId);

    /**
     * Поиск заявок по статусу.
     *
     * @param statusId идентификатор статуса заявки
     * @return список заявок с указанным статусом
     */
    List<ApplicationEntity> findAllByStatusId(Long statusId);

    /**
     * Поиск заявок по идентификатору стажировки.
     *
     * @param internshipId идентификатор стажировки
     * @return список заявок оставленных на указанную стажировку
     */
    List<ApplicationEntity> findAllByInternshipId(Long internshipId);

    /**
     * Поиск заявок по статусу и идентификатору стажировки.
     *
     * @param internshipId идентификатор стажировки
     * @param statusId     идентификатор статуса заявки
     * @return список заявок с указанным статусом и оставленных на указанную стажировку
     */
    List<ApplicationEntity> findAllByInternshipIdAndStatusId(Long internshipId, Long statusId);
}
