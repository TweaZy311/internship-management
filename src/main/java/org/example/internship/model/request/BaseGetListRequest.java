package org.example.internship.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.example.internship.model.SearchCriteria;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class BaseGetListRequest {
    private List<SearchCriteria> filters = new ArrayList<>();
    private int page;
    private int pageSize;
    private String sortBy;
    private String sortDirection;
}