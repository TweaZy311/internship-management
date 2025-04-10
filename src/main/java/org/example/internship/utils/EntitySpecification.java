package org.example.internship.utils;

import lombok.RequiredArgsConstructor;
import org.example.internship.model.SearchCriteria;
import org.example.internship.model.SearchOperation;
import org.springframework.data.jpa.domain.Specification;


import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class EntitySpecification<T> implements Specification<T> {
    private final SearchCriteria criteria;

    @Override
    @SuppressWarnings("unchecked")
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Expression<?> path = getPath(root, criteria.getKey().getColumn());
        if (criteria.getOperation().equals(SearchOperation.IN)) {
            return path.in((List) criteria.getValue());
        }
        if (path.getJavaType() == LocalDate.class) {
            return createPredicate((Expression<LocalDate>) path, LocalDate.parse(criteria.getValue().toString()), criteriaBuilder);
        } else if (path.getJavaType() == LocalDateTime.class) {
            return createPredicate((Expression<LocalDateTime>) path, LocalDateTime.parse(criteria.getValue().toString()), criteriaBuilder);
        } else if (path.getJavaType() == Date.class) {
            return createPredicate((Expression<Date>) path, Date.from(ZonedDateTime.parse(criteria.getValue().toString()).toInstant()), criteriaBuilder);
        } else if (path.getJavaType() == Boolean.class) {
            return createPredicate((Expression<Boolean>) path, (Boolean) criteria.getValue(), criteriaBuilder);
        } else {
            return createPredicate((Expression<String>) path, criteria.getValue().toString(), criteriaBuilder);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable> Predicate createPredicate(Expression<T> path, T value, CriteriaBuilder criteriaBuilder) {
        if (criteria.getOperation().equals(SearchOperation.LIKE)) {
            return criteriaBuilder.like((Expression<String>) path, "%" + value + "%");
        } else if (criteria.getOperation().equals(SearchOperation.LIKE_IGNORE_CASE)) {
            return criteriaBuilder.like(criteriaBuilder.lower((Expression<String>) path), ("%" + criteria.getValue() + "%").toLowerCase());
        } else if (criteria.getOperation().equals(SearchOperation.STARTS_WITH)) {
            return criteriaBuilder.like((Expression<String>) path, value + "%");
        } else if (criteria.getOperation().equals(SearchOperation.EQ)) {
            return criteriaBuilder.equal(path, value);
        } else if (criteria.getOperation().equals(SearchOperation.EQUALS_IGNORE_CASE)) {
            return criteriaBuilder.equal(criteriaBuilder.lower((Expression<String>) path), ((String) value).toLowerCase());
        } else if (criteria.getOperation().equals(SearchOperation.NEQ)) {
            return criteriaBuilder.notEqual(path, value);
        } else if (criteria.getOperation().equals(SearchOperation.GT)) {
            return criteriaBuilder.greaterThan(path, value);
        } else if (criteria.getOperation().equals(SearchOperation.GE)) {
            return criteriaBuilder.greaterThanOrEqualTo(path, value);
        } else if (criteria.getOperation().equals(SearchOperation.LT)) {
            return criteriaBuilder.lessThan(path, value);
        } else if (criteria.getOperation().equals(SearchOperation.LE)) {
            return criteriaBuilder.lessThanOrEqualTo(path, value);
        } else if (criteria.getOperation().equals(SearchOperation.IN)) {
            return criteriaBuilder.in(path.in(value));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T, R> Path<R> getPath(Path<T> root, String column) {
        String[] pathElements = column.split("\\.");
        Path<?> path = root;
        for (String pathElement : pathElements) {
            path = path.get(pathElement);
        }
        return (Path<R>) path;
    }
}
