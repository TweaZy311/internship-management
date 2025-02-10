package org.example.internship.dto;


import lombok.Getter;
import lombok.Setter;
import org.example.internship.model.StatusType;

@Getter
@Setter
public class StatusDto {
    private Long id;
    private String name;
    private StatusType type;
}
