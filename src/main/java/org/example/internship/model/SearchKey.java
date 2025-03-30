package org.example.internship.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchKey {
    ID("id"),
    STATUS_ID("status.id"),
    STATUS_NAME("status.name"),
    STATUS_TYPE("status.type"),
    INTERNSHIP_ID("internship.id"),
    USERNAME("username"),
    ROLE("role");

    private final String column;
}
