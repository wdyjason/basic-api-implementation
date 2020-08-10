package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.exception.ContentEmptyException;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VoteService {

    private VoteRepository voteRepository;

    private UserRepository userRepository;

    public VoteService(VoteRepository voteRepository, UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
    }

    public HttpStatus voteForEvent(int eventId, VoteDto voteDto) throws ContentEmptyException {
        Optional<UserEntity> voteUserWarp = userRepository.findById(voteDto.getUserId());
        if (!voteUserWarp.isPresent()) throw new ContentEmptyException("not find user");
        if (voteUserWarp.get().getTickets() >= voteDto.getVoteNum()) {
            voteRepository.save(voteDto.toEntity());
        } else {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }

    public List<VoteDto> getVoteListBytime(LocalDateTime start, LocalDateTime end) {
        return voteRepository.findByVoteTimeBetween(start, end)
                .stream()
                .map(VoteDto ::fromEntity)
                .collect(Collectors.toList());
    }
}
