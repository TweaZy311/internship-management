package org.example.internship.entity;

/**
 * Перечисление ролей пользователей.
 */
public enum UserRole {
    USER("Пользователь"),
    ADMIN("Администратор"),
    ARCHIVED("Архивирован");
    private final String name;

    UserRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
