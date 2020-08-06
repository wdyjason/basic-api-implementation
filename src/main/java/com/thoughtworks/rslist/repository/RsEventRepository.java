package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.entity.RsEventEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RsEventRepository extends CrudRepository<RsEventEntity, Integer> {

    List<RsEventEntity> findAll();

    @Modifying
    @Query("UPDATE RsEventEntity r SET r.keyWord=?2 WHERE r.id=?1")
    void updateKeyWordById(Integer id, String keyWord);

    @Modifying
    @Query("UPDATE RsEventEntity r SET r.eventName=?2 WHERE r.id=?1")
    void updateEventNameById(Integer id, String eventName);
}
