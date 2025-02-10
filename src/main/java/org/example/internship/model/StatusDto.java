package org.example.internship.model;


import lombok.Getter;
import lombok.Setter;
import org.example.internship.entity.StatusType;

@Getter
@Setter
public class StatusDto {
    private Long id;
    private String name;
    private StatusType type;
}
