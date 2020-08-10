package com.thoughtworks.rslist.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.rslist.entity.RsEventEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsEventDto {

    @NotNull
    private Integer id;

    private String eventName;

    private String keyword;

    private Integer voteNum;

    private Integer userId;

    public static RsEventDto fromEntity(RsEventEntity entity) {
        int sumVote = 0;
        if (entity.getVoteEntities() != null && entity.getVoteEntities().size() > 0) {
            sumVote = entity.getVoteEntities().stream().map(f -> f.getVoteNum()).mapToInt(v -> v).sum();
        }
        return RsEventDto.builder()
                .id(entity.getId())
                .eventName(entity.getEventName())
                .keyword(entity.getKeyword())
                .voteNum(sumVote)
                .build();
    }
    @JsonIgnore
    public Integer getUserId() {
        return userId;
    }

    @JsonProperty
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public RsEventEntity toEntity() {
        return RsEventEntity.builder()
                .id(id)
                .userId(userId)
                .eventName(eventName)
                .keyword(keyword)
                .build();
    }
}
