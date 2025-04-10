package org.example.internship.model;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class Page<T>{
    private List<? extends T> content;
    private Long totalElements;
    private int totalPages;
    private int pageSize;
    private int pageNumber;
}
