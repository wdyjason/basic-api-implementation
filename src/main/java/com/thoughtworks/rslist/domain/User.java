package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.rslist.validation.FirstValidation;
import com.thoughtworks.rslist.validation.SecondValidation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Size(max = 8, groups = SecondValidation.class)
    @NotNull(groups = FirstValidation.class)
    @JsonProperty("user_name")
    private String userName;

    @Min(value = 18, groups = SecondValidation.class)
    @Max(value = 100, groups = SecondValidation.class)
    @NotNull(groups = FirstValidation.class)
    @JsonProperty("user_age")
    private Integer age;

    @NotNull(groups = FirstValidation.class)
    @JsonProperty("user_gender")
    private String gender;

    @Email(groups = FirstValidation.class)
    @JsonProperty("user_email")
    private String email;

    @NotNull(groups = FirstValidation.class)
    @Pattern(regexp = "1\\d{10}", groups = SecondValidation.class)
    @JsonProperty("user_phone")
    private String phone;
}
