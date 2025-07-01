// src/main/java/pack/repository/chat/ChatMessageRepository.java
package pack.repository.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pack.model.chat.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    
    // ✅ ASC로 변경 - 오래된 메시지가 위, 최신 메시지가 아래
    @Query("SELECT cm FROM ChatMessage cm " +
           "LEFT JOIN FETCH cm.sender " +
           "WHERE cm.chatRoomId = :roomId " +
           "ORDER BY cm.sentAt ASC")
    Page<ChatMessage> findByChatRoomIdWithSenderDetails(@Param("roomId") Integer roomId, Pageable pageable);
    
    // ✅ ASC로 변경
    Page<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Integer roomId, Pageable pageable);
    
    // 최신 메시지 조회는 DESC 유지
    ChatMessage findTopByChatRoomIdOrderBySentAtDesc(Integer roomId);
}
