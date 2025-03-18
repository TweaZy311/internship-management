package org.example.internship.mapper;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class LocalDateConverter extends BidirectionalConverter<Long, LocalDate> {
    @Override
    public LocalDate convertTo(Long source, Type<LocalDate> destinationType) {
        return Instant.ofEpochMilli(source).atOffset(ZoneOffset.UTC).toLocalDate();
    }

    @Override
    public Long convertFrom(LocalDate source, Type<Long> destinationType) {
        return source.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
