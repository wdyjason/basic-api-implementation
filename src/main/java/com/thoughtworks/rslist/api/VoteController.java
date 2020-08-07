package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.exception.ContentEmptyException;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class VoteController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteRepository voteRepository;

    @PostMapping("/rs/{rsEventId}/vote")
    public ResponseEntity voteForEvent(@PathVariable Integer rsEventId,
                                       @RequestBody VoteEntity voteEntity) throws ContentEmptyException {
        Optional<UserEntity> voteUserWarp = userRepository.findById(voteEntity.getUserId());
        if (!voteUserWarp.isPresent()) throw new ContentEmptyException("not find user");
            if (voteUserWarp.get().getTickets() >= voteEntity.getVoteNum()) {
                voteRepository.save(voteEntity);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/rs/history/vote")
    public List<VoteEntity> getList(@RequestParam String startTime, @RequestParam String endTime) {

        return voteRepository.findByVoteTimeBetween(LocalDateTime.parse(startTime), LocalDateTime.parse(endTime));
    }
}
