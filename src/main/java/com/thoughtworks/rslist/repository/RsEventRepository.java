package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.entity.RsEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RsEventRepository extends CrudRepository<RsEventEntity, Integer> {

    List<RsEventEntity> findAll();

    @Modifying(flushAutomatically = true)
    @Query("UPDATE RsEventEntity r SET r.keyWord=?2 WHERE r.id=?1")
    void updateKeyWordById(Integer id, String keyWord);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE RsEventEntity r SET r.eventName=?2 WHERE r.id=?1")
    void updateEventNameById(Integer id, String eventName);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE RsEventEntity r SET r.eventName = :#{#entity.eventName}, r.keyWord = :#{#entity.keyWord} WHERE r.id = :#{#entity.id}")
    void updateRsEventEntityById(RsEventEntity entity);

    List<RsEventEntity> findAll(Pageable page);
}
