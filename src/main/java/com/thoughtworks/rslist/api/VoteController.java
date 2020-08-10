package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.exception.ContentEmptyException;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import com.thoughtworks.rslist.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class VoteController {

    private VoteService voteService;

    public VoteController(VoteService voteService) {
      this.voteService = voteService;
    }

    @PostMapping("/rs/{rsEventId}/vote")
    public ResponseEntity voteForEvent(@PathVariable Integer rsEventId,
                                       @RequestBody VoteDto voteDto) throws ContentEmptyException {
      return ResponseEntity.status(voteService.voteForEvent(rsEventId, voteDto)).build();
    }

    @GetMapping("/rs/history/vote")
    public List<VoteDto> getList(@RequestParam String startTime, @RequestParam String endTime) {
        return voteService.getVoteListBytime(LocalDateTime.parse(startTime), LocalDateTime.parse(endTime));
    }
}
