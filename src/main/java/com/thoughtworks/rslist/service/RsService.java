package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.exception.ContentEmptyException;
import com.thoughtworks.rslist.exception.OutOfIndexException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.thoughtworks.rslist.utils.Utils.strIsBlank;


public class RsService {

    UserRepository userRepository;
    VoteRepository voteRepository;
    RsEventRepository rsEventRepository;

    public RsService(UserRepository userRepository, VoteRepository voteRepository, RsEventRepository rsEventRepository) {
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.rsEventRepository = rsEventRepository;
    }


    public List<RsEventDto> findAllRsEvent(Integer pageIndex, Integer pageSize) {

        if (pageIndex != null && pageSize != null) {
            Pageable page = PageRequest.of(pageIndex - 1, pageSize);
            return rsEventRepository.findAll(page)
                    .stream()
                    .map(RsEventDto::fromEntity)
                    .collect(Collectors.toList());
        }
        return rsEventRepository.findAll()
                .stream()
                .map(RsEventDto::fromEntity)
                .collect(Collectors.toList());
    }

    public RsEventDto getOneRsEventById(int id) throws OutOfIndexException {
        if (!rsEventRepository.existsById(id)) {
            throw new OutOfIndexException("invalid id");
        }
        return RsEventDto.fromEntity(rsEventRepository.findById(id).get());
    }

    public void createRsEvent(RsEventEntity newEvent) throws ContentEmptyException {
        if (userRepository.existsById(newEvent.getUserId())) {
            rsEventRepository.save(newEvent);
        } else {
            throw new ContentEmptyException("invalid user to add rsEvent");
        }
    }

    @Transactional
    public void updateRsEvent(RsEventDto rsEventDTO) {
        if (!userRepository.existsById(rsEventDTO.getUserId())) {
            throw new RuntimeException("this user has not been registered");
        }
        rsEventRepository.updateRsEventEntityById(rsEventDTO.toEntity());
    }

    public void deleteRsEvent(int id) {
        rsEventRepository.deleteById(id);
    }

    @Transactional
    public boolean updatePartProperty(int id, String eventName, String keyword) {
        if (rsEventRepository.existsById(id)) {

            if (strIsBlank(eventName)) {
                rsEventRepository.updateEventNameById(id, eventName);
            }

            if (strIsBlank(keyword)) {
                rsEventRepository.updateKeywordById(id, keyword);
            }
            return true;
        }
        return false;
    }


}
