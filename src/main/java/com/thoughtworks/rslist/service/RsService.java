package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.DTO.RsEventDTO;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.exception.ContentEmptyException;
import com.thoughtworks.rslist.exception.OutOfIndexException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.thoughtworks.rslist.utils.Utils.strIsBlank;


public class RsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    RsEventRepository rsEventRepository;

    public List<RsEventDTO> findAllRsEvent(Integer pageIndex, Integer pageSize) {

        if (pageIndex != null && pageSize != null) {
            Pageable page = PageRequest.of(pageIndex - 1, pageSize);
            return rsEventRepository.findAll(page)
                    .stream()
                    .map(RsEventDTO::fromEntity)
                    .collect(Collectors.toList());
        }
        return rsEventRepository.findAll()
                .stream()
                .map(RsEventDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public RsEventDTO getOneRsEventById(int id) throws OutOfIndexException {
        if (!rsEventRepository.existsById(id)) {
            throw new OutOfIndexException("invalid id");
        }
        return RsEventDTO.fromEntity(rsEventRepository.findById(id).get());
    }

    public void createRsEvent(RsEventEntity newEvent) throws ContentEmptyException {
        if (userRepository.existsById(newEvent.getUserId())) {
            rsEventRepository.save(newEvent);
        } else {
            throw new ContentEmptyException("invalid user to add rsEvent");
        }
    }

    @Transactional
    public void updateRsEvent(RsEventDTO rsEventDTO) {
        if (!userRepository.existsById(rsEventDTO.getUserId())) {
            throw new RuntimeException("this user has not been registered");
        }
        rsEventRepository.updateRsEventEntityById(rsEventDTO.toEntity());
    }

    public void deleteRsEvent(int id) {
        rsEventRepository.deleteById(id);
    }

    @Transactional
    public boolean updatePartProperty(int id, String eventName, String keyWord) {
        if (rsEventRepository.existsById(id)) {

            if (strIsBlank(eventName)) {
                rsEventRepository.updateEventNameById(id, eventName);
            }

            if (strIsBlank(keyWord)) {
                rsEventRepository.updateKeyWordById(id, keyWord);
            }
            return true;
        }
        return false;
    }


}
