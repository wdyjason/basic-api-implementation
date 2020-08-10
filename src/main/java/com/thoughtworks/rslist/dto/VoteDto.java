package com.thoughtworks.rslist.dto;

import com.thoughtworks.rslist.entity.VoteEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteDto {

    private LocalDateTime voteTime;

    private Integer voteNum;

    private Integer userId;

    private Integer rsEventEntityId;

    public VoteEntity toEntity() {
        return VoteEntity.builder()
                .rsEventEntityId(rsEventEntityId)
                .userId(userId)
                .voteNum(voteNum)
                .voteTime(voteTime)
                .build();
    }

    public static VoteDto fromEntity(VoteEntity entity) {
        return VoteDto.builder()
                .userId(entity.getUserId())
                .rsEventEntityId(entity.getRsEventEntityId())
                .voteNum(entity.getVoteNum())
                .voteTime(entity.getVoteTime())
                .build();
    }
}
