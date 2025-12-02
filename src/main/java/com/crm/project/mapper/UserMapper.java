package com.crm.project.mapper;

import com.crm.project.dto.request.UserCreationRequest;
import com.crm.project.dto.request.UserUpdateRequest;
import com.crm.project.dto.response.UserResponse;
import com.crm.project.entity.User;
import com.crm.project.internal.UserNormalInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    UserNormalInfo toUserNormalInfo(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UserUpdateRequest request, @MappingTarget User user);
}
