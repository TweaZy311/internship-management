package org.example.internship.model.response.internship;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO, для получения публичной информации о стажировке.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class PublicInternshipInfo extends Internship {
}
