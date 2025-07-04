package pack.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.stereotype.Controller;
import pack.dto.chat.ChatMessageRequest;
import pack.dto.chat.ChatMessageResponse;
import pack.service.chat.ChatMessageService;
import pack.service.chat.ChatRoomService;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessageSendingOperations messagingTemplate;

    private static final String SYSTEM_SENDER = "SYSTEM";
    private static final String SYSTEM_NICKNAME = "시스템";

    @MessageMapping("/chat/message")
    public void sendMessage(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        String senderId = extractAndValidateSenderId(payload, headerAccessor);
        Integer chatRoomId = extractAndValidateRoomId(payload);
        String messageText = extractAndValidateMessageText(payload);

        ChatMessageRequest request = new ChatMessageRequest();
        request.setChatRoomId(chatRoomId);
        request.setMessageText(messageText);

        ChatMessageResponse response = chatMessageService.sendMessage(request, senderId);

        messagingTemplate.convertAndSend("/topic/chat/room/" + chatRoomId, response);
    }

    @MessageMapping("/chat/join/{chatRoomId}")
    public void joinRoom(@DestinationVariable("chatRoomId") Integer chatRoomId,
                         @Payload Map<String, Object> payload,
                         SimpMessageHeaderAccessor headerAccessor) {
        String memberId = extractAndValidateMemberId(payload, headerAccessor);
        chatRoomService.joinChatRoom(chatRoomId, memberId);
        updateSessionAttributes(headerAccessor, chatRoomId, memberId);
        ChatMessageResponse joinMessage = createSystemMessage(chatRoomId, memberId + "님이 채팅방에 입장했습니다.");
        messagingTemplate.convertAndSend("/topic/chat/room/" + chatRoomId, joinMessage);
    }

    @MessageMapping("/chat/leave/{chatRoomId}")
    public void leaveRoom(@DestinationVariable("chatRoomId") Integer chatRoomId,
                          @Payload Map<String, Object> payload,
                          SimpMessageHeaderAccessor headerAccessor) {
        String memberId = extractAndValidateMemberId(payload, headerAccessor);
        chatRoomService.leaveChatRoom(chatRoomId, memberId);
        ChatMessageResponse leaveMessage = createSystemMessage(chatRoomId, memberId + "님이 채팅방에서 퇴장했습니다.");
        messagingTemplate.convertAndSend("/topic/chat/room/" + chatRoomId, leaveMessage);
    }

    // 이하 입력 검증, 세션 속성, 시스템 메시지 메서드는 기존 코드와 동일하게 사용
    private String extractAndValidateSenderId(Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        String senderId = (String) payload.get("senderId");
        if (senderId == null && headerAccessor.getSessionAttributes() != null) {
            senderId = (String) headerAccessor.getSessionAttributes().get("username");
        }
        if (senderId == null || senderId.trim().isEmpty()) {
            throw new IllegalArgumentException("발신자 ID가 누락되었습니다");
        }
        return senderId.trim();
    }

    private Integer extractAndValidateRoomId(Map<String, Object> payload) {
        Object roomIdObj = payload.get("chatRoomId");
        if (roomIdObj == null) roomIdObj = payload.get("roomId");
        if (roomIdObj == null) throw new IllegalArgumentException("채팅방 ID가 누락되었습니다");
        Integer roomId;
        if (roomIdObj instanceof Integer) roomId = (Integer) roomIdObj;
        else if (roomIdObj instanceof String) roomId = Integer.parseInt((String) roomIdObj);
        else throw new IllegalArgumentException("올바르지 않은 채팅방 ID 타입입니다");
        if (roomId <= 0) throw new IllegalArgumentException("채팅방 ID는 양수여야 합니다");
        return roomId;
    }

    private String extractAndValidateMessageText(Map<String, Object> payload) {
        String messageText = (String) payload.get("messageText");
        if (messageText == null) messageText = (String) payload.get("message");
        if (messageText == null || messageText.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용이 누락되었습니다");
        }
        String trimmedText = messageText.trim();
        if (trimmedText.length() > 1000) {
            throw new IllegalArgumentException("메시지가 너무 깁니다 (최대 1000자): " + trimmedText.length() + "자");
        }
        return trimmedText;
    }

    private String extractAndValidateMemberId(Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        String memberId = (String) payload.get("memberId");
        if (memberId == null && headerAccessor.getSessionAttributes() != null) {
            memberId = (String) headerAccessor.getSessionAttributes().get("username");
        }
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID가 누락되었습니다");
        }
        return memberId.trim();
    }

    private void updateSessionAttributes(SimpMessageHeaderAccessor headerAccessor, Integer chatRoomId, String memberId) {
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("chatRoomId", chatRoomId);
            headerAccessor.getSessionAttributes().put("username", memberId);
            headerAccessor.getSessionAttributes().put("lastActivity", System.currentTimeMillis());
        }
    }

    private ChatMessageResponse createSystemMessage(Integer chatRoomId, String message) {
        return ChatMessageResponse.builder()
                .chatRoomId(chatRoomId)
                .senderId(SYSTEM_SENDER)
                .senderNickname(SYSTEM_NICKNAME)
                .messageText(message)
                .sentAt(LocalDateTime.now())
                .build();
    }
}
