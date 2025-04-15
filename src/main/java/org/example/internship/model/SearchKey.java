package org.example.internship.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchKey {
    ID("id"),
    STATUS_ID("status.id"), //application, solution
    STATUS_NAME("status.name"), //application, solution
    STATUS_TYPE("status.type"), //application, solution
    INTERNSHIP_ID("internship.id"), //application, lesson
    USER_ID("user.id"),
    USER_USERNAME("user.username"),
    USER_EMAIL("user.email"),
    USER_ROLE("user.role"),
    USERNAME("username"), //user
    ROLE("role"), //user
    //internship
    START_DATE("startDate"), //internship
    END_DATE("endDate"), //internship
    REGISTRATION_START_DATE("registrationStartDate"), //internship
    REGISTRATION_END_DATE("registrationEndDate"), //internship
    IS_OPEN("isOpen"), //internship
    //solution
    TASK_ID("task.id"),
    //status
    TYPE("type"),
    //task
    LESSON_ID("lesson.id"),
    IS_PUBLISHED("isPublished"), //task, lesson
    //audit
    ACTION_TYPE("actionType"),
    ENTITY_TYPE("entityType"),
    ENTITY_ID("entityId");

    private final String column;
}
