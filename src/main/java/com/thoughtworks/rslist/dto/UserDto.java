package com.thoughtworks.rslist.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.validation.FirstValidation;
import com.thoughtworks.rslist.validation.SecondValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @Size(max = 8, groups = SecondValidation.class)
    @NotNull(groups = FirstValidation.class)
    @JsonProperty("user_name")
    @JsonAlias("userName")
    private String userName;

    @Min(value = 18, groups = SecondValidation.class)
    @Max(value = 100, groups = SecondValidation.class)
    @NotNull(groups = FirstValidation.class)
    @JsonAlias("age")
    @JsonProperty("user_age")
    private Integer age;

    @NotNull(groups = FirstValidation.class)
    @JsonProperty("user_gender")
    @JsonAlias("gender")
    private String gender;

    @Email(groups = FirstValidation.class)
    @JsonProperty("user_email")
    @JsonAlias("email")
    private String email;

    @NotNull(groups = FirstValidation.class)
    @Pattern(regexp = "1\\d{10}", groups = SecondValidation.class)
    @JsonProperty("user_phone")
    @JsonAlias("phone")
    private String phone;

    public UserEntity toEntity() {
        return UserEntity.builder()
                .userName(userName)
                .age(age)
                .email(email)
                .gender(gender)
                .phone(phone)
                .build();
    }

    public static UserDto formUserEntity(UserEntity userEntity) {
        return UserDto.builder()
                .userName(userEntity.getUserName())
                .age(userEntity.getAge())
                .gender(userEntity.getGender())
                .phone(userEntity.getPhone())
                .email(userEntity.getEmail())
                .build();
    }
}
