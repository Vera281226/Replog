package pack.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import pack.dto.chat.ChatRoomResponse;
import pack.dto.chat.ChatMessageRequest;
import pack.dto.chat.ChatMessageResponse;
import pack.service.chat.AiChatService;
import pack.service.chat.ChatMessageService;
import pack.service.chat.ChatRoomService;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final AiChatService aiChatService;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @PostMapping("/ai")
    public ResponseEntity<ChatMessageResponse> sendAiMessage(
            @RequestParam(value = "message", required = true) String message,
            HttpServletRequest request) {
        String memberId = extractMemberId(request);
        if (memberId == null || message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ChatMessageResponse response = aiChatService.sendAiMessage(message.trim(), memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rooms/ai")
    public ResponseEntity<ChatRoomResponse> createAiRoom(HttpServletRequest request) {
        String memberId = extractMemberId(request);
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        ChatRoomResponse response = aiChatService.getOrCreateAiChatRoom(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(HttpServletRequest request) {
        String memberId = extractMemberId(request);
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        List<ChatRoomResponse> rooms = chatRoomService.getMyChatRooms(memberId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getMessages(
            @PathVariable("roomId") Integer roomId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size,
            HttpServletRequest request) {
        String memberId = extractMemberId(request);
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessageResponse> messages = chatMessageService.getChatMessages(roomId, memberId, pageable);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestBody ChatMessageRequest request,
            HttpServletRequest httpRequest) {
        String senderId = extractMemberId(httpRequest);
        if (senderId == null) {
            return ResponseEntity.status(401).build();
        }
        ChatMessageResponse response = chatMessageService.sendMessage(request, senderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<?> joinChatRoom(
            @PathVariable("roomId") Integer roomId,
            HttpServletRequest request) {
        String memberId = extractMemberId(request);
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        boolean joined = chatRoomService.joinChatRoom(roomId, memberId);
        if (!joined) {
            // 이미 참가자인 경우 409 Conflict 반환
            return ResponseEntity.status(409).body("이미 채팅방에 참가한 사용자입니다.");
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(
            @PathVariable("roomId") Integer roomId,
            HttpServletRequest request) {
        String memberId = extractMemberId(request);
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        ChatRoomResponse response = chatRoomService.getChatRoom(roomId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/rooms/{roomId}/leave")
    public ResponseEntity<Void> leaveChatRoom(
            @PathVariable("roomId") Integer roomId,
            HttpServletRequest request) {
        String memberId = extractMemberId(request);
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        chatRoomService.leaveChatRoom(roomId, memberId);
        return ResponseEntity.ok().build();
    }

    private String extractMemberId(HttpServletRequest request) {
        // 기존 extractMemberId 로직 그대로 사용
        try {
            Object memberIdObj = request.getSession(false) != null
                    ? request.getSession(false).getAttribute("loginMember")
                    : null;
            if (memberIdObj == null) {
                memberIdObj = request.getSession(false) != null
                        ? request.getSession(false).getAttribute("memberId")
                        : null;
            }
            if (memberIdObj instanceof String) {
                String memberId = (String) memberIdObj;
                if (!memberId.trim().isEmpty()) {
                    return memberId.trim();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    @GetMapping("/rooms/party-post/{partyPostNo}")
    public ResponseEntity<ChatRoomResponse> getRoomByPartyPost(@PathVariable(name="partyPostNo") Integer partyPostNo) {
        ChatRoomResponse response = chatRoomService.getRoomByPartyPostNo(partyPostNo);
        return ResponseEntity.ok(response);
    }
}
