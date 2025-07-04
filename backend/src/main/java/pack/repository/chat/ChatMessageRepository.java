package pack.repository.chat;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import pack.model.chat.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    
    // 최신 메시지를 아래로 설정하기 위한 쿼리 설정
    @Query("SELECT cm FROM ChatMessage cm " +
           "LEFT JOIN FETCH cm.sender " +
           "WHERE cm.chatRoomId = :roomId " +
           "ORDER BY cm.sentAt ASC")
    Page<ChatMessage> findByChatRoomIdWithSenderDetails(@Param("roomId") Integer roomId, Pageable pageable);
    
    Page<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Integer roomId, Pageable pageable);
    List<ChatMessage> findTop10ByChatRoomIdOrderBySentAtDesc(Integer chatRoomId);
}
