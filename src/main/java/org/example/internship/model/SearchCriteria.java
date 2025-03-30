package org.example.internship.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    private SearchKey key;
    private SearchOperation operation;
    private Object value;
    private BooleanOperator operator;
}
