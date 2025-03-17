//package org.example.internship.mapper;
//
//import org.example.internship.model.request.CreateMessageRequest;
//import org.example.internship.model.response.Message;
//import org.example.internship.entity.MessageEntity;
//import org.example.internship.entity.user.UserEntity;
//import org.example.internship.repository.UserRepository;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.Named;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.persistence.EntityNotFoundException;
//
///**
// * Маппер для сущности Message.
// */
//@Mapper(componentModel = "spring")
//public abstract class MessageMapper {
//    private UserRepository userRepository;
//
//    @Autowired
//    public void setUserRepository(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    /**
//     * Преобразование DTO для создания нового сообщения (NewMessageDto) в сущность Message.
//     *
//     * @param dto DTO для создания нового сообщения
//     * @return сущность Message
//     */
//    @Mapping(target = "sentAt", expression = "java(java.time.LocalDateTime.now())")
//    @Mapping(target = "receiver", source = "receiverId", qualifiedByName = "getUserById")
//    @Mapping(target = "sender", source = "senderId", qualifiedByName = "getUserById")
//    public abstract MessageEntity newDtoToModel(CreateMessageRequest dto);
//
//    /**
//     * Преобразование сущности Message в DTO для ответа (MessageDto).
//     *
//     * @param messageEntity сущность Message
//     * @return DTO для ответа
//     */
//    @Mapping(target = "receiverId", source = "receiver", qualifiedByName = "getUserId")
//    @Mapping(target = "senderId", source = "sender", qualifiedByName = "getUserId")
//    public abstract Message modelToDto(MessageEntity messageEntity);
//
//    /**
//     * Получение сущности User по ID.
//     *
//     * @param userId ID пользователя
//     * @return сущность User
//     * @throws EntityNotFoundException если пользователь с указанным ID не найден
//     */
//    @Named(value = "getUserById")
//    public UserEntity getUserById(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
//    }
//
//    /**
//     * Получение ID пользователя.
//     *
//     * @param user сущность User
//     * @return ID пользователя
//     */
//    @Named(value = "getUserId")
//    public Long getUserId(UserEntity user) {
//        return user.getId();
//    }
//}
