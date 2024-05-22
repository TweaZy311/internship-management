package org.example.internship.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = new Validator();
    }

    @Test
    void emailIsValid_withValidEmail_returnTrue() {
        assertTrue(validator.emailIsValid("valid@email.com"));
        assertTrue(validator.emailIsValid("valid.valid@email.com"));
        assertTrue(validator.emailIsValid("valid_valid@email.com"));
        assertTrue(validator.emailIsValid("valid-valid@email.com"));
    }

    @Test
    void emailIsValid_withInvalidEmail_returnFalse() {
        assertFalse(validator.emailIsValid("invalid_email"));
        assertFalse(validator.emailIsValid("@email.com"));
        assertFalse(validator.emailIsValid("invalid@"));
        assertFalse(validator.emailIsValid("invalid@.com"));
    }

    @Test
    void phoneNumberIsValid_withValidPhoneNumber_returnTrue() {
        assertTrue(validator.phoneNumberIsValid("+79123456789"));
        assertTrue(validator.phoneNumberIsValid("79123456789"));
        assertTrue(validator.phoneNumberIsValid("89123456789"));
    }

    @Test
    void phoneNumberIsValid_withInvalidPhoneNumber_returnFalse() {
        assertFalse(validator.phoneNumberIsValid("1234567890"));
        assertFalse(validator.phoneNumberIsValid("+1234567890"));
        assertFalse(validator.phoneNumberIsValid("invalid_number"));
        assertFalse(validator.phoneNumberIsValid("+791278"));
    }

    @Test
    void dateIsValid_withValidDates_returnTrue() {
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = startDate.plusDays(20);
        LocalDate registrationEndDate = startDate.minusDays(5);

        assertTrue(validator.dateIsValid(startDate, endDate, registrationEndDate));
    }

    @Test
    void dateIsValid_withInvalidDates_returnFalse() {
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = startDate.plusDays(20);
        LocalDate registrationEndDate = startDate.plusDays(5);

        assertFalse(validator.dateIsValid(startDate, endDate, registrationEndDate));
    }

    @Test
    void dateIsValid_withNullDates_returnFalse() {
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = startDate.plusDays(20);
        LocalDate registrationEndDate = startDate.minusDays(5);

        assertFalse(validator.dateIsValid(null, endDate, registrationEndDate));
        assertFalse(validator.dateIsValid(startDate, null, registrationEndDate));
        assertFalse(validator.dateIsValid(startDate, endDate, null));
    }

    @Test
    void dateIsValid_withFourValidDates_returnTrue() {
        LocalDate startDate = LocalDate.now().plusDays(20);
        LocalDate endDate = startDate.plusDays(10);
        LocalDate registrationStartDate = startDate.minusDays(15);
        LocalDate registrationEndDate = startDate.minusDays(5);

        assertTrue(validator.dateIsValid(startDate, endDate, registrationStartDate, registrationEndDate));
    }

    @Test
    void dateIsValid_withFourInvalidDates_returnFalse() {
        LocalDate startDate = LocalDate.now().plusDays(20);
        LocalDate endDate = startDate.plusDays(10);
        LocalDate registrationStartDate = startDate.minusDays(5);
        LocalDate registrationEndDate = startDate.minusDays(15);

        assertFalse(validator.dateIsValid(startDate, endDate, registrationStartDate, registrationEndDate));
    }

    @Test
    void dateIsValid_withFourNullDates_returnFalse() {
        LocalDate startDate = LocalDate.now().plusDays(20);
        LocalDate endDate = startDate.plusDays(10);
        LocalDate registrationStartDate = startDate.minusDays(15);
        LocalDate registrationEndDate = startDate.minusDays(5);

        assertFalse(validator.dateIsValid(null, endDate, registrationStartDate, registrationEndDate));
        assertFalse(validator.dateIsValid(startDate, null, registrationStartDate, registrationEndDate));
        assertFalse(validator.dateIsValid(startDate, endDate, null, registrationEndDate));
        assertFalse(validator.dateIsValid(startDate, endDate, registrationStartDate, null));
    }
}