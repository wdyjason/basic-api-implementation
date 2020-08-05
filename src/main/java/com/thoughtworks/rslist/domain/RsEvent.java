package com.thoughtworks.rslist.domain;

import com.thoughtworks.rslist.validation.FirstValidation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsEvent {

    @NotNull(groups = FirstValidation.class)
    private String eventName;

    @NotNull(groups = FirstValidation.class)
    private String keyWord;

    @NotNull(groups = FirstValidation.class)
    @Valid
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
