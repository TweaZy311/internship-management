package org.example.internship.service.application;

import org.example.internship.dto.request.application.ApplicationStatusDto;
import org.example.internship.dto.request.application.NewApplicationDto;
import org.example.internship.dto.response.application.ApplicationDto;

import java.util.List;

/**
 * Сервис для работы с заявками.
 */
public interface ApplicationService {
    /**
     * Сохранение новой заявки.
     *
     * @param application информация о новой заявке
     */
    void save(NewApplicationDto application);

    /**
     * Изменение статуса заявки.
     *
     * @param statusDto объект, содержащий идентификатор заявки и новый статус
     */
    void changeStatus(ApplicationStatusDto statusDto);

    /**
     * Получение всех заявок.
     *
     * @return список всех заявок
     */
    List<ApplicationDto> getAll();

    /**
     * Получение заявки по её идентификатору.
     *
     * @param id идентификатор заявки
     * @return информация о заявке с указанным идентификатором
     */
    ApplicationDto getById(Long id);

    /**
     * Получение списка заявок по статусу.
     *
     * @param status статус заявки
     * @return список заявок с указанным статусом
     */
    List<ApplicationDto> getByStatus(String status);


    /**
     * Получение списка заявок по идентификатору стажировки.
     *
     * @param internshipId идентификатор стажировки
     * @return список заявок, оставленных на указанную стажировку
     */
    List<ApplicationDto> getAllByInternshipId(Long internshipId);

    /**
     * Получение списка заявок по идентификатору стажировки и статусу.
     *
     * @param internshipId идентификатор стажировки
     * @param status       статус заявки
     * @return список заявок, оставленных на указанную стажировку с указанным статусом
     */
    List<ApplicationDto> getAllByInternshipIdAndStatus(Long internshipId, String status);
}
