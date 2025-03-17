//package org.example.internship.mapper;
//
//import org.example.internship.model.request.CreateUserRequest;
//import org.example.internship.model.response.User;
//import org.example.internship.entity.internship.InternshipEntity;
//import org.example.internship.entity.user.UserEntity;
//import org.example.internship.repository.InternshipRepository;
//import org.mapstruct.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import javax.persistence.EntityNotFoundException;
//
///**
// * Маппер для сущности User.
// */
//@Mapper(componentModel = "spring")
//public abstract class UserMapper {
//    private InternshipRepository internshipRepository;
//    private PasswordEncoder passwordEncoder;
//
//    @Value("${user.password}")
//    private String password;
//
//    @Autowired
//    public void setInternshipRepository(InternshipRepository internshipRepository) {
//        this.internshipRepository = internshipRepository;
//    }
//
//    @Autowired
//    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    /**
//     * Преобразование DTO для пользователя (UserDto) в сущность User.
//     *
//     * @param user DTO для пользователя
//     * @return сущность User
//     */
//    @Mapping(target = "internship", source = "internshipId", qualifiedByName = "getInternshipById")
//    public abstract UserEntity dtoToModel(User user);
//
//    /**
//     * Преобразование DTO для создания нового пользователя (NewUserDto) в сущность User.
//     *
//     * @param newUserDto DTO для создания нового пользователя
//     * @return сущность User
//     */
//    @Mapping(target = "role", expression = "java(org.example.internship.model.user.Role.USER)")
//    @Mapping(target = "internship", source = "internshipId", qualifiedByName = "getInternshipById")
//    public abstract UserEntity newDtoToModel(CreateUserRequest newUserDto);
//
//    /**
//     * Преобразование сущности User в DTO для ответа (UserDto).
//     *
//     * @param user сущность User
//     * @return DTO для ответа
//     */
//    @Mapping(target = "internshipId", source = "internship", qualifiedByName = "getInternshipId")
//    public abstract User modelToDto(UserEntity user);
//
//
//    /**
//     * Получение сущности Internship по ID.
//     *
//     * @param id ID стажировки
//     * @return сущность Internship
//     * @throws EntityNotFoundException если стажировка с указанным ID не найдена
//     */
//    @Named(value = "getInternshipById")
//    public InternshipEntity getInternshipById(Long id) {
//        return internshipRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Internship not found with ID: " + id));
//    }
//
//    /**
//     * Получение ID стажировки.
//     *
//     * @param internship сущность Internship
//     * @return ID стажировки
//     */
//    @Named(value = "getInternshipId")
//    public Long getInternshipId(InternshipEntity internship) {
//        return internship.getId();
//    }
//
//    /**
//     * Установка пароля пользователя после маппинга NewUserDto в сущность User.
//     *
//     * @param user DTO NewUserDto
//     * @param userBuilder билдер для сущности User
//     */
//    @AfterMapping
//    public void setPassword(CreateUserRequest user, @MappingTarget UserEntity.UserBuilder userBuilder) {
//        userBuilder.password(passwordEncoder.encode(password)).build();
//    }
//
//}
