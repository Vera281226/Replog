// src/main/java/pack/controller/chat/ChatController.java
package pack.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import pack.dto.chat.ChatRoomResponse;
import pack.request.chat.ChatMessageRequest;
import pack.response.chat.ChatMessageResponse;
import pack.service.chat.ChatService;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    
    private final ChatService chatService;
    
    /**
     * AI 메시지 전송
     * POST /api/chat/ai?message=메시지내용
     */
    @PostMapping("/ai")
    public ResponseEntity<ChatMessageResponse> sendAiMessage(
            @RequestParam(value = "message", required = true) String message,
            HttpServletRequest request) {
        
        try {
            // 입력값 검증
            if (message == null || message.trim().isEmpty()) {
                log.warn("AI 메시지 전송 요청에서 메시지가 누락됨");
                return ResponseEntity.badRequest().build();
            }
            
            String memberId = extractMemberId(request);
            if (memberId == null) {
                log.warn("AI 메시지 전송 요청에서 사용자 인증 실패");
                return ResponseEntity.status(401).build();
            }
            
            log.info("AI 메시지 전송 요청: 사용자={}, 메시지길이={}", memberId, message.trim().length());
            
            ChatMessageResponse response = chatService.sendAiMessage(message.trim(), memberId);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("AI 메시지 전송 요청 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("AI 메시지 전송 실패", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * AI 채팅방 생성/조회
     * POST /api/chat/rooms/ai
     */
    @PostMapping("/rooms/ai")
    public ResponseEntity<ChatRoomResponse> createAiRoom(HttpServletRequest request) {
        try {
            String memberId = extractMemberId(request);
            if (memberId == null) {
                log.warn("AI 채팅방 생성 요청에서 사용자 인증 실패");
                return ResponseEntity.status(401).build();
            }
            
            log.info("AI 채팅방 생성/조회 요청: 사용자={}", memberId);
            
            ChatRoomResponse response = chatService.getOrCreateAiChatRoom(memberId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("AI 채팅방 생성 실패", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 사용자의 채팅방 목록 조회
     * GET /api/chat/rooms
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(HttpServletRequest request) {
        try {
            String memberId = extractMemberId(request);
            if (memberId == null) {
                log.warn("채팅방 목록 조회 요청에서 사용자 인증 실패");
                return ResponseEntity.status(401).build();
            }
            
            log.info("채팅방 목록 조회 요청: 사용자={}", memberId);
            
            List<ChatRoomResponse> rooms = chatService.getChatRooms(memberId);
            return ResponseEntity.ok(rooms);
            
        } catch (Exception e) {
            log.error("채팅방 목록 조회 실패", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 특정 채팅방의 메시지 목록 조회 (페이지네이션)
     * GET /api/chat/rooms/{roomId}/messages?page=0&size=50
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getMessages(
            @PathVariable(value = "roomId", required = true) Integer roomId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "50") int size,
            HttpServletRequest request) {
        
        try {
            // 입력값 검증
            if (roomId == null || roomId <= 0) {
                log.warn("메시지 조회 요청에서 올바르지 않은 채팅방 ID: {}", roomId);
                return ResponseEntity.badRequest().build();
            }
            
            if (page < 0) page = 0;
            if (size <= 0 || size > 100) size = 50; // 최대 100개로 제한
            
            String memberId = extractMemberId(request);
            if (memberId == null) {
                log.warn("메시지 조회 요청에서 사용자 인증 실패");
                return ResponseEntity.status(401).build();
            }
            
            log.info("메시지 조회 요청: 사용자={}, 채팅방={}, 페이지={}, 크기={}", 
                    memberId, roomId, page, size);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<ChatMessageResponse> messages = chatService.getChatMessages(roomId, memberId, pageable);
            return ResponseEntity.ok(messages);
            
        } catch (IllegalArgumentException e) {
            log.warn("메시지 조회 요청 검증 실패: roomId={}, {}", roomId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("메시지 조회 실패: roomId={}", roomId, e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 일반 메시지 전송
     * POST /api/chat/messages
     */
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestBody ChatMessageRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // 입력값 검증
            if (request == null) {
                log.warn("메시지 전송 요청에서 요청 본문이 누락됨");
                return ResponseEntity.badRequest().build();
            }
            
            if (request.getChatRoomId() == null || request.getChatRoomId() <= 0) {
                log.warn("메시지 전송 요청에서 올바르지 않은 채팅방 ID: {}", request.getChatRoomId());
                return ResponseEntity.badRequest().build();
            }
            
            if (request.getMessageText() == null || request.getMessageText().trim().isEmpty()) {
                log.warn("메시지 전송 요청에서 메시지 내용이 누락됨");
                return ResponseEntity.badRequest().build();
            }
            
            String senderId = extractMemberId(httpRequest);
            if (senderId == null) {
                log.warn("메시지 전송 요청에서 사용자 인증 실패");
                return ResponseEntity.status(401).build();
            }
            
            // 메시지 길이 제한
            String messageText = request.getMessageText().trim();
            if (messageText.length() > 1000) {
                log.warn("메시지 전송 요청에서 메시지가 너무 김: {} 글자", messageText.length());
                return ResponseEntity.badRequest().build();
            }
            
            request.setMessageText(messageText);
            
            log.info("메시지 전송 요청: 사용자={}, 채팅방={}, 메시지길이={}", 
                    senderId, request.getChatRoomId(), messageText.length());
            
            ChatMessageResponse response = chatService.sendMessage(request, senderId);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("메시지 전송 요청 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("메시지 전송 실패", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 채팅방 참가
     * POST /api/chat/rooms/{roomId}/join
     */
    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<Void> joinChatRoom(
            @PathVariable(value = "roomId", required = true) Integer roomId,
            HttpServletRequest request) {
        
        try {
            // 입력값 검증
            if (roomId == null || roomId <= 0) {
                log.warn("채팅방 참가 요청에서 올바르지 않은 채팅방 ID: {}", roomId);
                return ResponseEntity.badRequest().build();
            }
            
            String memberId = extractMemberId(request);
            if (memberId == null) {
                log.warn("채팅방 참가 요청에서 사용자 인증 실패");
                return ResponseEntity.status(401).build();
            }
            
            log.info("채팅방 참가 요청: 사용자={}, 채팅방={}", memberId, roomId);
            
            chatService.joinChatRoom(roomId, memberId);
            return ResponseEntity.ok().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("채팅방 참가 요청 검증 실패: roomId={}, {}", roomId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("채팅방 참가 실패: roomId={}", roomId, e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 특정 채팅방 정보 조회
     * GET /api/chat/rooms/{roomId}
     */
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(
            @PathVariable(value = "roomId", required = true) Integer roomId,
            HttpServletRequest request) {
        
        try {
            // 입력값 검증
            if (roomId == null || roomId <= 0) {
                log.warn("채팅방 정보 조회 요청에서 올바르지 않은 채팅방 ID: {}", roomId);
                return ResponseEntity.badRequest().build();
            }
            
            String memberId = extractMemberId(request);
            if (memberId == null) {
                log.warn("채팅방 정보 조회 요청에서 사용자 인증 실패");
                return ResponseEntity.status(401).build();
            }
            
            log.info("채팅방 정보 조회 요청: 사용자={}, 채팅방={}", memberId, roomId);
            
            ChatRoomResponse response = chatService.getChatRoom(roomId);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("채팅방 정보 조회 요청 검증 실패: roomId={}, {}", roomId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("채팅방 정보 조회 실패: roomId={}", roomId, e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 채팅방 퇴장
     * DELETE /api/chat/rooms/{roomId}/leave
     */
    @DeleteMapping("/rooms/{roomId}/leave")
    public ResponseEntity<Void> leaveChatRoom(
            @PathVariable(value = "roomId", required = true) Integer roomId,
            HttpServletRequest request) {
        
        try {
            // 입력값 검증
            if (roomId == null || roomId <= 0) {
                log.warn("채팅방 퇴장 요청에서 올바르지 않은 채팅방 ID: {}", roomId);
                return ResponseEntity.badRequest().build();
            }
            
            String memberId = extractMemberId(request);
            if (memberId == null) {
                log.warn("채팅방 퇴장 요청에서 사용자 인증 실패");
                return ResponseEntity.status(401).build();
            }
            
            log.info("채팅방 퇴장 요청: 사용자={}, 채팅방={}", memberId, roomId);
            
            chatService.leaveChatRoom(roomId, memberId);
            return ResponseEntity.ok().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("채팅방 퇴장 요청 검증 실패: roomId={}, {}", roomId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("채팅방 퇴장 실패: roomId={}", roomId, e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 세션에서 사용자 ID 추출 (안전한 방식)
     * @param request HTTP 요청
     * @return 사용자 ID 또는 null
     */
    private String extractMemberId(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                // 세션 속성명 확인 - loginMember 또는 memberId
                Object memberIdObj = session.getAttribute("loginMember");
                if (memberIdObj == null) {
                    memberIdObj = session.getAttribute("memberId");
                }
                
                if (memberIdObj instanceof String) {
                    String memberId = (String) memberIdObj;
                    if (!memberId.trim().isEmpty()) {
                        return memberId.trim();
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.warn("사용자 ID 추출 실패", e);
            return null;
        }
    }
}
