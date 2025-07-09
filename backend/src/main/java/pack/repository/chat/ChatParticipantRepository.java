package pack.repository.chat;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pack.model.chat.ChatParticipant;
import pack.model.chat.ChatParticipantId;

import java.util.List;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, ChatParticipantId> {
    
    List<ChatParticipant> findByChatRoomId(Integer chatRoomId);
    
    List<ChatParticipant> findByMemberId(String memberId);
    
    boolean existsByChatRoomIdAndMemberId(Integer chatRoomId, String memberId);
    
    long countByChatRoomId(Integer chatRoomId);

    @Modifying
    @Query("DELETE FROM ChatParticipant cp WHERE cp.chatRoomId = :chatRoomId")
    void deleteAllByChatRoomId(@Param("chatRoomId") Integer chatRoomId);
}