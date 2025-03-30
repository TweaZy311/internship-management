package org.example.internship.service.application;

import org.example.internship.entity.ApplicationEntity;
import org.example.internship.model.request.GetApplicationsRequest;
import org.example.internship.model.request.application.UpdateApplicationStatusRequest;
import org.example.internship.model.request.application.CreateApplicationRequest;
import org.example.internship.model.response.application.Application;
import org.springframework.data.domain.Page;

/**
 * Сервис для работы с заявками.
 */
public interface ApplicationService {
    /**
     * Сохранение новой заявки.
     *
     * @param application информация о новой заявке
     */
    Application saveApplication(CreateApplicationRequest application);

    /**
     * Изменение статуса заявки.
     *
     * @param statusDto объект, содержащий идентификатор заявки и новый статус
     */
    Application changeApplicationStatus(UpdateApplicationStatusRequest statusDto);

    /**
     * Получение всех заявок.
     *
     * @return список всех заявок
     */
    Page<ApplicationEntity> getApplications(GetApplicationsRequest request);

    /**
     * Получение заявки по её идентификатору.
     *
     * @param id идентификатор заявки
     * @return информация о заявке с указанным идентификатором
     */
    Application getApplicationById(Long id);
}
