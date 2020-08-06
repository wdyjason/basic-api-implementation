package com.thoughtworks.rslist.entity;

import com.thoughtworks.rslist.validation.PatchForRsEventValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@Builder
@Table(name = "rs_event")
@NoArgsConstructor
@AllArgsConstructor
public class RsEventEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    private String eventName;

    @NotNull
    private String keyWord;

    @NotNull(groups = PatchForRsEventValidation.class)
    private Integer userId;

}
