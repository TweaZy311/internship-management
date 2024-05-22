package org.example.internship.service.internship;

import org.example.internship.dto.request.internship.InternshipStatusDto;
import org.example.internship.dto.request.internship.NewInternshipDto;
import org.example.internship.dto.request.internship.UpdateInternshipDto;
import org.example.internship.dto.response.ReportDto;
import org.example.internship.dto.response.internship.AdminInternshipDto;
import org.example.internship.dto.response.internship.PublicInternshipDto;

import java.util.List;

/**
 * Сервис для работы со стажировками.
 */
public interface InternshipService {

    /**
     * Сохранение новой стажировки.
     *
     * @param newInternshipDto информация о новой стажировке
     */
    void save(NewInternshipDto newInternshipDto);

    /**
     * Изменение статуса стажировки.
     *
     * @param statusDto информация о статусе стажировки
     */
    void changeStatus(InternshipStatusDto statusDto);

    /**
     * Изменение данных о стажировке.
     *
     * @param internshipDto обновленная информация о стажировке
     */
    void update(UpdateInternshipDto internshipDto);

    /**
     * Получение публичной информации о стажировке по идентификатору.
     *
     * @param id идентификатор стажировки
     * @return публичная информация о стажировке
     */
    PublicInternshipDto getById(Long id);

    /**
     * Получение списка открытых стажировок.
     *
     * @return список открытых стажировок
     */
    List<PublicInternshipDto> getOpened();

    /**
     * Получение списка стажировок по заданному статусу.
     *
     * @param status статус стажировки
     * @return список стажировок с заданным статусом
     */
    List<AdminInternshipDto> getByStatus(String status);

    /**
     * Получение списка всех стажировок.
     *
     * @return список всех стажировок
     */
    List<AdminInternshipDto> getAll();

    /**
     * Создание ведомости по стажировке.
     *
     * @param internshipId идентификатор стажировки
     * @return ведомость по стажировке
     */
    List<ReportDto> createReport(Long internshipId);
}
