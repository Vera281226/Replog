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
import pack.util.AuthUtil;

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
    public ResponseEntity sendAiMessage(
            @RequestParam(value = "message", required = true) String message) {
        String memberId = AuthUtil.getCurrentMemberId();
        if (memberId == null || message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ChatMessageResponse response = aiChatService.sendAiMessage(message.trim(), memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rooms/ai")
    public ResponseEntity createAiRoom() {
        String memberId = AuthUtil.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        ChatRoomResponse response = aiChatService.getOrCreateAiChatRoom(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms() {
        String memberId = AuthUtil.getCurrentMemberId();
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
            @RequestParam(value = "size", defaultValue = "50") int size) {
        String memberId = AuthUtil.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessageResponse> messages = chatMessageService.getChatMessages(roomId, memberId, pageable);
        return ResponseEntity.ok(messages);
    }


    @PostMapping("/messages")
    public ResponseEntity sendMessage(@RequestBody ChatMessageRequest request) {
        String senderId = AuthUtil.getCurrentMemberId();
        if (senderId == null) {
            return ResponseEntity.status(401).build();
        }
        ChatMessageResponse response = chatMessageService.sendMessage(request, senderId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<?> joinChatRoom(@PathVariable("roomId") Integer roomId) {
        String memberId = AuthUtil.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        boolean joined = chatRoomService.joinChatRoom(roomId, memberId);
        if (!joined) {
            return ResponseEntity.status(409).body("이미 채팅방에 참가한 사용자입니다.");
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity getChatRoom(@PathVariable("roomId") Integer roomId) {
        String memberId = AuthUtil.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        ChatRoomResponse response = chatRoomService.getChatRoom(roomId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/rooms/{roomId}/leave")
    public ResponseEntity leaveChatRoom(@PathVariable("roomId") Integer roomId) {
        String memberId = AuthUtil.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        chatRoomService.leaveChatRoom(roomId, memberId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/rooms/party-post/{partyPostNo}")
    public ResponseEntity<ChatRoomResponse> getRoomByPartyPost(@PathVariable(name="partyPostNo") Integer partyPostNo) {
        ChatRoomResponse response = chatRoomService.getRoomByPartyPostNo(partyPostNo);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/rooms/{roomId}/participants")
    public ResponseEntity<String> removeParticipant(@PathVariable("roomId") Integer roomId) {
        String memberId = AuthUtil.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        
        try {
            chatRoomService.removeParticipant(roomId, memberId);
            return ResponseEntity.ok("채팅방에서 성공적으로 나갔습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("채팅방 나가기에 실패했습니다: " + e.getMessage());
        }
    }
}
