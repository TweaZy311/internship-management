package org.example.internship.service.lesson;

import lombok.RequiredArgsConstructor;
import org.example.internship.entity.InternshipEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.lesson.CreateLessonRequest;
import org.example.internship.entity.LessonEntity;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.repository.InternshipRepository;
import org.example.internship.repository.LessonRepository;
import org.example.internship.utils.SpecificationsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для работы с занятиями.
 */
@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final String LESSON_WITH_SUCH_ID_COULD_NOT_BE_FOUND = "Lesson with such ID could not be found";

    private final LessonRepository lessonRepository;
    private final InternshipRepository internshipRepository;
    private final Mapper mapper;

    /**
     * {@inheritDoc}
     *
     * @param createLessonRequest информация о новом занятии
     */
    @Override
    public LessonEntity saveLesson(CreateLessonRequest createLessonRequest) {
        LessonEntity lessonEntity = mapper.map(createLessonRequest, LessonEntity.class);
        InternshipEntity internship = internshipRepository.findById(createLessonRequest.getInternshipId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), "Internship with such ID could not be found"));
        lessonEntity.setInternship(internship);
        return lessonRepository.save(lessonEntity);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор занятия
     * @return информация о занятии
     * @throws ServiceException если занятие не найдено
     */
    @Override
    public LessonEntity getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.LSN_404.getCode(), LESSON_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех занятий
     */
    @Override
    public Page<LessonEntity> getLessons(BaseGetListRequest request) {
        SpecificationsBuilder<LessonEntity> filterBuilder = new SpecificationsBuilder<>();
        request.getFilters().forEach(filterBuilder::with);

        Sort sort = request.getSortBy() != null ?
                Sort.by(
                        Sort.Direction.fromOptionalString(request.getSortDirection()).orElse(Sort.Direction.ASC),
                        request.getSortBy()
                ) :
                Sort.unsorted();
        return lessonRepository.findAll(filterBuilder.build(),
                PageRequest.of(request.getPage(), request.getPageSize(), sort));
    }

    /**
     * {@inheritDoc}
     *
     * @param internshipId идентификатор стажировки
     * @return список опубликованных занятий
     */
    @Override
    public List<LessonEntity> getAllPublishedByInternshipId(Long internshipId) {
        return lessonRepository.findByIsPublishedAndInternshipId(true, internshipId);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор занятия
     * @throws ServiceException если занятие не найдено
     */
    @Override
    public LessonEntity publishLesson(Long id) {
        LessonEntity lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.LSN_404.getCode(), LESSON_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        if (lesson.getIsPublished()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.LSN_400.getCode(), "Lesson is already published");
        }
        lesson.setIsPublished(true);
        return lessonRepository.save(lesson);
    }
}
