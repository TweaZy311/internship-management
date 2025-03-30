package org.example.internship.utils;


import org.example.internship.model.BooleanOperator;
import org.example.internship.model.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SpecificationsBuilder<T> {
    private final List<SearchCriteria> params;
    private Function<SearchCriteria, Specification<T>> specification;

    public SpecificationsBuilder() {
        this.params = new ArrayList<>();
        this.specification = EntitySpecification::new;
    }

    public SpecificationsBuilder with(SearchCriteria criteria) {
        params.add(criteria);
        return this;
    }

    public Specification<T> build() {
        Assert.notNull(specification, "Specification must be set");
        if (params.isEmpty()) {
            return null;
        }
        Specification<T> spec = Specification.where(specification.apply(params.remove(0)));
        for (SearchCriteria param : params) {
            spec = param.getOperator().equals(BooleanOperator.OR) ? spec.or(specification.apply(param)) : spec.and(specification.apply(param));
        }
        return spec;
    }
}
