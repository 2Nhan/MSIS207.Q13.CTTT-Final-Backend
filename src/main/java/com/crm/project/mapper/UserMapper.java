package com.crm.project.mapper;

import com.crm.project.dto.request.UserCreationRequest;
import com.crm.project.dto.response.UserResponse;
import com.crm.project.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
}
