package org.example.internship.model.response.internship;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.example.internship.model.response.User;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO, предназначенная администратору, для получения информации о стажировке.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PrivateInternshipInfo extends Internship {
//    private List<InternshipLessonInfo> lessons;
    private List<User> participants = new ArrayList<>();
}
