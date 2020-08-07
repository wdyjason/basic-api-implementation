package com.thoughtworks.rslist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "rs_user")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "name")
    @NotNull
    private String userName;

    private Integer age;

    private String gender;

    private String email;

    private String phone;

    private Integer tickets;

    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "userId")
    List<RsEventEntity> rsEventEntityList;
}
