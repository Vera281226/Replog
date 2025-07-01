// src/main/java/pack/controller/chat/WebSocketChatController.java
package pack.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import pack.request.chat.ChatMessageRequest;
import pack.response.chat.ChatMessageResponse;
import pack.service.chat.ChatService;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatController {
    
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;
    
    private static final String SYSTEM_SENDER = "SYSTEM";
    private static final String SYSTEM_NICKNAME = "시스템";
    
    /**
     * WebSocket을 통한 메시지 전송
     * 클라이언트에서 /app/chat/message로 전송
     */
    @MessageMapping("/chat/message")
    public void sendMessage(@Payload Map<String, Object> payload, 
                           SimpMessageHeaderAccessor headerAccessor) {
        try {
            // 입력값 검증 및 추출
            String senderId = extractAndValidateSenderId(payload, headerAccessor);
            Integer chatRoomId = extractAndValidateRoomId(payload);
            String messageText = extractAndValidateMessageText(payload);
            
            log.info("WebSocket 메시지 전송 요청: 발신자={}, 채팅방={}, 메시지길이={}", 
                    senderId, chatRoomId, messageText.length());
            
            // 메시지 요청 객체 생성
            ChatMessageRequest request = new ChatMessageRequest();
            request.setChatRoomId(chatRoomId);
            request.setMessageText(messageText);
            
            // 서비스를 통한 메시지 저장 및 처리
            ChatMessageResponse response = chatService.sendMessage(request, senderId);
            
            // 채팅방의 모든 참가자에게 브로드캐스트
            messagingTemplate.convertAndSend(
                "/topic/chat/room/" + chatRoomId, response
            );
            
            log.debug("WebSocket 메시지 전송 완료: 채팅방={}, 발신자={}, 메시지ID={}", 
                     chatRoomId, senderId, response.getChatMessagesId());
            
        } catch (IllegalArgumentException e) {
            log.warn("WebSocket 메시지 요청 검증 실패: {}", e.getMessage());
            sendErrorMessage(headerAccessor.getSessionId(), 
                           "올바른 메시지 형식이 아닙니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("WebSocket 메시지 전송 실패", e);
            sendErrorMessage(headerAccessor.getSessionId(), 
                           "메시지 전송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }
    
    /**
     * WebSocket을 통한 채팅방 입장
     * 클라이언트에서 /app/chat/join/{chatRoomId}로 전송
     */
    @MessageMapping("/chat/join/{chatRoomId}")
    public void joinRoom(@DestinationVariable(value = "chatRoomId") Integer chatRoomId,
                        @Payload Map<String, Object> payload,
                        SimpMessageHeaderAccessor headerAccessor) {
        try {
            // 입력값 검증
            if (chatRoomId == null || chatRoomId <= 0) {
                throw new IllegalArgumentException("올바르지 않은 채팅방 ID입니다");
            }
            
            String memberId = extractAndValidateMemberId(payload, headerAccessor);
            
            log.info("WebSocket 채팅방 입장 요청: 사용자={}, 채팅방={}", memberId, chatRoomId);
            
            // 채팅방 참가 처리
            chatService.joinChatRoom(chatRoomId, memberId);
            
            // 세션 속성 업데이트
            updateSessionAttributes(headerAccessor, chatRoomId, memberId);
            
            // 입장 알림 메시지 전송 (선택적)
            ChatMessageResponse joinMessage = createSystemMessage(
                chatRoomId, memberId + "님이 채팅방에 입장했습니다."
            );
            
            messagingTemplate.convertAndSend("/topic/chat/room/" + chatRoomId, joinMessage);
            
            log.info("WebSocket 채팅방 입장 완료: 채팅방={}, 사용자={}", chatRoomId, memberId);
            
        } catch (IllegalArgumentException e) {
            log.warn("WebSocket 채팅방 입장 요청 검증 실패: roomId={}, {}", chatRoomId, e.getMessage());
            sendErrorMessage(headerAccessor.getSessionId(), 
                           "채팅방 입장 요청이 올바르지 않습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("WebSocket 채팅방 입장 실패: roomId={}", chatRoomId, e);
            sendErrorMessage(headerAccessor.getSessionId(), 
                           "채팅방 입장에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }
    
    /**
     * WebSocket을 통한 채팅방 퇴장
     * 클라이언트에서 /app/chat/leave/{chatRoomId}로 전송
     */
    @MessageMapping("/chat/leave/{chatRoomId}")
    public void leaveRoom(@DestinationVariable(value = "chatRoomId") Integer chatRoomId,
                         @Payload Map<String, Object> payload,
                         SimpMessageHeaderAccessor headerAccessor) {
        try {
            // 입력값 검증
            if (chatRoomId == null || chatRoomId <= 0) {
                throw new IllegalArgumentException("올바르지 않은 채팅방 ID입니다");
            }
            
            String memberId = extractAndValidateMemberId(payload, headerAccessor);
            
            log.info("WebSocket 채팅방 퇴장 요청: 사용자={}, 채팅방={}", memberId, chatRoomId);
            
            // 채팅방 퇴장 처리
            chatService.leaveChatRoom(chatRoomId, memberId);
            
            // 퇴장 알림 메시지 전송 (선택적)
            ChatMessageResponse leaveMessage = createSystemMessage(
                chatRoomId, memberId + "님이 채팅방에서 퇴장했습니다."
            );
            
            messagingTemplate.convertAndSend("/topic/chat/room/" + chatRoomId, leaveMessage);
            
            log.info("WebSocket 채팅방 퇴장 완료: 채팅방={}, 사용자={}", chatRoomId, memberId);
            
        } catch (IllegalArgumentException e) {
            log.warn("WebSocket 채팅방 퇴장 요청 검증 실패: roomId={}, {}", chatRoomId, e.getMessage());
            sendErrorMessage(headerAccessor.getSessionId(), 
                           "채팅방 퇴장 요청이 올바르지 않습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("WebSocket 채팅방 퇴장 실패: roomId={}", chatRoomId, e);
            sendErrorMessage(headerAccessor.getSessionId(), 
                           "채팅방 퇴장에 실패했습니다.");
        }
    }
    
    /**
     * 하트비트/핑 메시지 처리
     * 클라이언트에서 /app/ping으로 전송
     */
    @MessageMapping("/ping")
    public void handlePing(@Payload Map<String, Object> payload,
                          SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            Long timestamp = (Long) payload.get("timestamp");
            
            log.trace("하트비트 수신: 세션={}, 타임스탬프={}", sessionId, timestamp);
            
            // Pong 응답 전송
            Map<String, Object> pongResponse = Map.of(
                "type", "pong",
                "timestamp", System.currentTimeMillis(),
                "clientTimestamp", timestamp != null ? timestamp : 0L
            );
            
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/pong", pongResponse);
            
        } catch (Exception e) {
            log.warn("하트비트 처리 실패: {}", e.getMessage());
        }
    }
    
    // ===== 입력값 검증 메서드들 =====
    
    /**
     * 발신자 ID 추출 및 검증
     */
    private String extractAndValidateSenderId(Map<String, Object> payload, 
                                            SimpMessageHeaderAccessor headerAccessor) {
        String senderId = (String) payload.get("senderId");
        
        // payload에서 찾지 못하면 세션에서 찾기
        if (senderId == null && headerAccessor.getSessionAttributes() != null) {
            senderId = (String) headerAccessor.getSessionAttributes().get("username");
        }
        
        if (senderId == null || senderId.trim().isEmpty()) {
            throw new IllegalArgumentException("발신자 ID가 누락되었습니다");
        }
        
        return senderId.trim();
    }
    
    /**
     * 채팅방 ID 추출 및 검증
     */
    private Integer extractAndValidateRoomId(Map<String, Object> payload) {
        Object roomIdObj = payload.get("chatRoomId");
        if (roomIdObj == null) {
            roomIdObj = payload.get("roomId"); // 대안 키 확인
        }
        
        if (roomIdObj == null) {
            throw new IllegalArgumentException("채팅방 ID가 누락되었습니다");
        }
        
        Integer roomId;
        if (roomIdObj instanceof Integer) {
            roomId = (Integer) roomIdObj;
        } else if (roomIdObj instanceof String) {
            try {
                roomId = Integer.parseInt((String) roomIdObj);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("올바르지 않은 채팅방 ID 형식입니다: " + roomIdObj);
            }
        } else {
            throw new IllegalArgumentException("올바르지 않은 채팅방 ID 타입입니다: " + roomIdObj.getClass().getSimpleName());
        }
        
        if (roomId <= 0) {
            throw new IllegalArgumentException("채팅방 ID는 양수여야 합니다: " + roomId);
        }
        
        return roomId;
    }
    
    /**
     * 메시지 텍스트 추출 및 검증
     */
    private String extractAndValidateMessageText(Map<String, Object> payload) {
        String messageText = (String) payload.get("messageText");
        if (messageText == null) {
            messageText = (String) payload.get("message"); // 대안 키 확인
        }
        
        if (messageText == null || messageText.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용이 누락되었습니다");
        }
        
        String trimmedText = messageText.trim();
        if (trimmedText.length() > 1000) {
            throw new IllegalArgumentException("메시지가 너무 깁니다 (최대 1000자): " + trimmedText.length() + "자");
        }
        
        return trimmedText;
    }
    
    /**
     * 사용자 ID 추출 및 검증
     */
    private String extractAndValidateMemberId(Map<String, Object> payload, 
                                            SimpMessageHeaderAccessor headerAccessor) {
        String memberId = (String) payload.get("memberId");
        
        // payload에서 찾지 못하면 세션에서 찾기
        if (memberId == null && headerAccessor.getSessionAttributes() != null) {
            memberId = (String) headerAccessor.getSessionAttributes().get("username");
        }
        
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID가 누락되었습니다");
        }
        
        return memberId.trim();
    }
    
    // ===== 헬퍼 메서드들 =====
    
    /**
     * 세션 속성 업데이트
     */
    private void updateSessionAttributes(SimpMessageHeaderAccessor headerAccessor, 
                                       Integer chatRoomId, String memberId) {
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("chatRoomId", chatRoomId);
            headerAccessor.getSessionAttributes().put("username", memberId);
            headerAccessor.getSessionAttributes().put("lastActivity", System.currentTimeMillis());
        }
    }
    
    /**
     * 시스템 메시지 생성
     */
    private ChatMessageResponse createSystemMessage(Integer chatRoomId, String message) {
        return ChatMessageResponse.builder()
            .chatRoomId(chatRoomId)
            .senderId(SYSTEM_SENDER)
            .senderNickname(SYSTEM_NICKNAME)
            .messageText(message)
            .sentAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 특정 세션에 에러 메시지 전송
     */
    private void sendErrorMessage(String sessionId, String message) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            log.warn("에러 메시지 전송 실패: 세션 ID가 누락됨");
            return;
        }
        
        try {
            ChatMessageResponse errorResponse = ChatMessageResponse.builder()
                .senderId(SYSTEM_SENDER)
                .senderNickname(SYSTEM_NICKNAME)
                .messageText(message)
                .sentAt(LocalDateTime.now())
                .build();
            
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/errors", errorResponse);
            log.debug("에러 메시지 전송 완료: 세션={}, 메시지={}", sessionId, message);
            
        } catch (Exception e) {
            log.error("에러 메시지 전송 실패: 세션={}, 메시지={}", sessionId, message, e);
        }
    }
    
    /**
     * 채팅방의 모든 사용자에게 시스템 알림 전송
     */
    private void broadcastSystemNotification(Integer chatRoomId, String message) {
        try {
            ChatMessageResponse notification = createSystemMessage(chatRoomId, message);
            messagingTemplate.convertAndSend("/topic/chat/room/" + chatRoomId, notification);
            log.debug("시스템 알림 브로드캐스트 완료: 채팅방={}, 메시지={}", chatRoomId, message);
        } catch (Exception e) {
            log.error("시스템 알림 브로드캐스트 실패: 채팅방={}, 메시지={}", chatRoomId, message, e);
        }
    }
}
