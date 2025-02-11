package org.example.internship.service.internship;

import org.example.internship.model.request.internship.UpdateInternshipStatusRequest;
import org.example.internship.model.request.internship.CreateInternshipRequest;
import org.example.internship.model.request.internship.UpdateInternshipRequest;
import org.example.internship.model.response.Report;
import org.example.internship.model.response.internship.PrivateInternshipInfo;
import org.example.internship.model.response.internship.PublicInternshipInfo;

import java.util.List;

/**
 * Сервис для работы со стажировками.
 */
public interface InternshipService {

    /**
     * Сохранение новой стажировки.
     *
     * @param createInternshipRequest информация о новой стажировке
     */
    void save(CreateInternshipRequest createInternshipRequest);

    /**
     * Изменение статуса стажировки.
     *
     * @param statusDto информация о статусе стажировки
     */
    void changeStatus(UpdateInternshipStatusRequest statusDto);

    /**
     * Изменение данных о стажировке.
     *
     * @param internshipDto обновленная информация о стажировке
     */
    void update(UpdateInternshipRequest internshipDto);

    /**
     * Получение публичной информации о стажировке по идентификатору.
     *
     * @param id идентификатор стажировки
     * @return публичная информация о стажировке
     */
    PublicInternshipInfo getById(Long id);

    /**
     * Получение списка открытых стажировок.
     *
     * @return список открытых стажировок
     */
    List<PublicInternshipInfo> getOpened();

    /**
     * Получение списка стажировок по заданному статусу.
     *
     * @param status статус стажировки
     * @return список стажировок с заданным статусом
     */
    List<PrivateInternshipInfo> getByStatus(String status);

    /**
     * Получение списка всех стажировок.
     *
     * @return список всех стажировок
     */
    List<PrivateInternshipInfo> getAll();

    /**
     * Создание ведомости по стажировке.
     *
     * @param internshipId идентификатор стажировки
     * @return ведомость по стажировке
     */
    List<Report> createReport(Long internshipId);
}
