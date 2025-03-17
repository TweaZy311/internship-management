package org.example.internship.service.internship;

import org.example.internship.model.request.internship.UpdateInternshipStatusRequest;
import org.example.internship.model.request.internship.CreateUpdateInternshipRequest;
import org.example.internship.model.response.Report;
import org.example.internship.model.response.internship.Internship;
import org.example.internship.model.response.internship.PublicInternshipInfo;

import java.util.List;

/**
 * Сервис для работы со стажировками.
 */
public interface InternshipService {

    /**
     * Сохранение новой стажировки.
     *
     * @param createUpdateInternshipRequest информация о новой стажировке
     */
    void save(CreateUpdateInternshipRequest createUpdateInternshipRequest);

    /**
     * Изменение статуса стажировки.
     *
     * @param statusDto информация о статусе стажировки
     */
    void changeStatus(UpdateInternshipStatusRequest statusDto);

    /**
     * Изменение данных о стажировке.
     *
     * @param createUpdateInternshipRequest обновленная информация о стажировке
     */
    void update(Long id, CreateUpdateInternshipRequest createUpdateInternshipRequest);

    /**
     * Получение публичной информации о стажировке по идентификатору.
     *
     * @param id идентификатор стажировки
     * @return публичная информация о стажировке
     */
    Internship getById(Long id, Boolean isPrivate);

    /**
     * Получение списка открытых стажировок.
     *
     * @return список открытых стажировок
     */
    List<PublicInternshipInfo> getByIsOpen(Boolean isOpen);

    /**
     * Получение списка стажировок по заданному статусу.
     *
     * @param statusId идентификатор статуса стажировки
     * @return список стажировок с заданным статусом
     */
    List<Internship> getByStatus(Long statusId);

    /**
     * Получение списка всех стажировок.
     *
     * @return список всех стажировок
     */
    List<Internship> getAll();

    /**
     * Создание ведомости по стажировке.
     *
     * @param internshipId идентификатор стажировки
     * @return ведомость по стажировке
     */
    List<Report> createReport(Long internshipId);
}
