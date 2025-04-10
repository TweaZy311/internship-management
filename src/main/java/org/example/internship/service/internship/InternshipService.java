package org.example.internship.service.internship;

import org.example.internship.entity.InternshipEntity;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.internship.UpdateInternshipStatusRequest;
import org.example.internship.model.request.internship.CreateUpdateInternshipRequest;
import org.example.internship.model.response.Report;
import org.example.internship.model.response.internship.Internship;
import org.example.internship.model.response.internship.PublicInternshipInfo;
import org.springframework.data.domain.Page;

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
    Internship saveInternship(CreateUpdateInternshipRequest createUpdateInternshipRequest);

    /**
     * Изменение статуса стажировки.
     *
     * @param statusDto информация о статусе стажировки
     */
    Internship changeInternshipStatus(UpdateInternshipStatusRequest statusDto);

    /**
     * Изменение данных о стажировке.
     *
     * @param createUpdateInternshipRequest обновленная информация о стажировке
     */
    Internship updateInternship(Long id, CreateUpdateInternshipRequest createUpdateInternshipRequest);

    /**
     * Получение публичной информации о стажировке по идентификатору.
     *
     * @param id идентификатор стажировки
     * @return публичная информация о стажировке
     */
    Internship getInternshipById(Long id, Boolean isPrivate);

    /**
     * Получение списка открытых стажировок.
     *
     * @return список открытых стажировок
     */
    List<PublicInternshipInfo> getInternshipsByIsOpen(Boolean isOpen);

    /**
     * Получение списка стажировок по заданному статусу.
     *
     * @param statusId идентификатор статуса стажировки
     * @return список стажировок с заданным статусом
     */
    List<Internship> getInternshipsByStatus(Long statusId);

    /**
     * Получение списка всех стажировок.
     *
     * @return список всех стажировок
     */
    Page<InternshipEntity> getInternships(BaseGetListRequest request);

    /**
     * Создание ведомости по стажировке.
     *
     * @param internshipId идентификатор стажировки
     * @return ведомость по стажировке
     */
    List<Report> createReport(Long internshipId);
}
