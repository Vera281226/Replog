// src/main/java/pack/repository/chat/ChatRoomRepository.java
package pack.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pack.model.chat.ChatRoom;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    
    List<ChatRoom> findByIsActiveTrue();
    
    Optional<ChatRoom> findByChatRoomIdAndIsActiveTrue(Integer chatRoomId);
    
    Optional<ChatRoom> findByPartyPostNo(Integer partyPostNo);
    
    // ✅ 올바른 JOIN 쿼리 (participants 관계 사용)
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
           "JOIN cr.participants cp " +
           "WHERE cp.memberId = :memberId AND cr.isActive = true " +
           "ORDER BY cr.createdAt DESC")
    List<ChatRoom> findByMemberIdWithDetails(@Param("memberId") String memberId);
    
    // ✅ AI 채팅방 조회 (수정된 방식)
    @Query("SELECT cr FROM ChatRoom cr " +
           "JOIN cr.participants cp " +
           "WHERE cp.memberId = :memberId AND cr.roomType = :roomType AND cr.isActive = true")
    Optional<ChatRoom> findByRoomTypeAndMemberId(@Param("roomType") ChatRoom.RoomType roomType, 
                                                @Param("memberId") String memberId);
}
