package org.example.internship.service.application;

import org.example.internship.model.request.application.UpdateApplicationStatusRequest;
import org.example.internship.model.request.application.CreateApplicationRequest;
import org.example.internship.model.response.application.Application;

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
    void save(CreateApplicationRequest application);

    /**
     * Изменение статуса заявки.
     *
     * @param statusDto объект, содержащий идентификатор заявки и новый статус
     */
    void changeStatus(UpdateApplicationStatusRequest statusDto);

    /**
     * Получение всех заявок.
     *
     * @return список всех заявок
     */
    List<Application> getAll();

    /**
     * Получение заявки по её идентификатору.
     *
     * @param id идентификатор заявки
     * @return информация о заявке с указанным идентификатором
     */
    Application getById(Long id);

    /**
     * Получение списка заявок по статусу.
     *
     * @param statusId идентификатор статуса заявки
     * @return список заявок с указанным статусом
     */
    List<Application> getByStatus(Long statusId);


    /**
     * Получение списка заявок по идентификатору стажировки.
     *
     * @param internshipId идентификатор стажировки
     * @return список заявок, оставленных на указанную стажировку
     */
    List<Application> getAllByInternship(Long internshipId);

    /**
     * Получение списка заявок по идентификатору стажировки и статусу.
     *
     * @param internshipId идентификатор стажировки
     * @param statusId     идентификатор статуса заявки
     * @return список заявок, оставленных на указанную стажировку с указанным статусом
     */
    List<Application> getAllByInternshipAndStatus(Long internshipId, Long statusId);
}
