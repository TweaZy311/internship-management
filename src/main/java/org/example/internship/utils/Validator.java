package org.example.internship.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для валидации различных типов данных.
 */
@Component
public class Validator {
    private final String EMAIL_FORMAT = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private final String PHONE_NUMBER_FORMAT = "^((\\+7|7|8)+([0-9]){10})$";

    /**
     * Проверка корректности формата электронной почты.
     *
     * @param email адрес электронной почты для проверки
     * @return true, если адрес электронной почты соответствует формату, в противном случае false
     */
    public boolean emailIsValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_FORMAT);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Проверка корректности формата номера телефона.
     *
     * @param phoneNumber номер телефона для проверки
     * @return true, если номер телефона соответствует формату, в противном случае false
     */
    public boolean phoneNumberIsValid(String phoneNumber) {
        Pattern pattern = Pattern.compile(PHONE_NUMBER_FORMAT);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    /**
     * Проверка корректности дат.
     *
     * @param startDate           начальная дата стажировки
     * @param endDate             конечная дата стажировки
     * @param registrationEndDate дата окончания регистрации
     * @return true, если даты корректны, в противном случае false
     */
    public boolean dateIsValid(LocalDate startDate, LocalDate endDate, LocalDate registrationEndDate) {
        if (startDate == null || endDate == null || registrationEndDate == null) {
            return false;
        }
        return startDate.isBefore(endDate) &&
                registrationEndDate.isAfter(LocalDate.now()) &&
                registrationEndDate.isBefore(startDate);
    }

    /**
     * Проверка корректности дат.
     *
     * @param startDate             начальная дата стажировки
     * @param endDate               конечная дата стажировки
     * @param registrationStartDate дата начала регистрации
     * @param registrationEndDate   дата окончания регистрации
     * @return true, если даты корректны, в противном случае false
     */
    public boolean dateIsValid(LocalDate startDate, LocalDate endDate,
                               LocalDate registrationStartDate, LocalDate registrationEndDate) {
        if (startDate == null || endDate == null ||
                registrationStartDate == null || registrationEndDate == null) {
            return false;
        }
        return registrationStartDate.isBefore(registrationEndDate) &&
                registrationEndDate.isBefore(startDate) &&
                startDate.isBefore(endDate);
    }
}
