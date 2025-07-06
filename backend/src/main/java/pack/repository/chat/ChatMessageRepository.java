package pack.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pack.model.chat.ChatMessage;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    // 최근 10개 메시지 조회 (내림차순)
    List<ChatMessage> findTop10ByChatRoom_ChatRoomIdOrderBySentAtDesc(Integer chatRoomId);

    // 채팅방별 메시지 전체 조회 (오름차순)
    Page<ChatMessage> findByChatRoom_ChatRoomIdOrderBySentAtAsc(Integer chatRoomId, Pageable pageable);

    // JPQL 쿼리 예시 (sender까지 fetch)
    @Query("SELECT cm FROM ChatMessage cm LEFT JOIN FETCH cm.sender WHERE cm.chatRoom.chatRoomId = :roomId ORDER BY cm.sentAt ASC")
    List<ChatMessage> findAllWithSenderByChatRoomId(@Param("roomId") Integer chatRoomId);

    // 필요시 추가 메서드도 모두 chatRoom_ChatRoomId 형태로 작성
}
