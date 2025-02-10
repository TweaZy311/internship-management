package org.example.internship.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.example.internship.dto.request.application.NewApplicationDto;
import org.example.internship.model.application.Application;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Mapper extends ConfigurableMapper {
    private final ObjectMapper objectMapper;

    @Override
    protected void configure(MapperFactory factory) {
        factory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDateTime.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDate.class));

        factory.classMap(NewApplicationDto.class, Application.class)
                .byDefault()
                .register();


    }
}
