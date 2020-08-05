package com.thoughtworks.rslist.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Size(max = 8)
     private String userName;

    private Integer age;

    private String gender;

    private String email;

    private String phone;
}
