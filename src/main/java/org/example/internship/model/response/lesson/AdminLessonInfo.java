package org.example.internship.model.response.lesson;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.example.internship.model.response.internship.Internship;
import org.example.internship.model.response.task.ShortTaskInfo;

import java.util.List;

/**
 * DTO, предназначенная администратору, для получения информации о занятии.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class AdminLessonInfo extends Lesson {
    private Boolean isPublished;
    private List<ShortTaskInfo> tasks;
    private Internship internship;
}
