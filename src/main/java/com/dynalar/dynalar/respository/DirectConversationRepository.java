package com.dynalar.dynalar.respository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dynalar.dynalar.model.chatmodel.DirectConversation;

public interface DirectConversationRepository extends JpaRepository<DirectConversation, Long> {

   Optional<DirectConversation> findByUserLowIdAndUserHighId(Long lowId, Long highId);

   @Query("select c from DirectConversation c where c.userLow.id = :uid or c.userHigh.id = :uid")
   List<DirectConversation> findAllByUser(@Param("uid") Long uid);
}
