// src/main/java/pack/service/chat/ChatServiceImpl.java
package pack.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pack.dto.chat.ChatRoomRequest;
import pack.dto.chat.ChatRoomResponse;
import pack.model.chat.ChatMessage;
import pack.model.chat.ChatParticipant;
import pack.model.chat.ChatParticipantId;
import pack.model.chat.ChatRoom;
import pack.model.member.Member;
import pack.repository.chat.ChatMessageRepository;
import pack.repository.chat.ChatParticipantRepository;
import pack.repository.chat.ChatRoomRepository;
import pack.repository.member.MemberRepository;
import pack.request.chat.ChatMessageRequest;
import pack.response.chat.ChatMessageResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatClient chatClient;
    
    // ✅ AI 채팅방 생성 중복 방지
    private final ConcurrentHashMap<String, CompletableFuture<ChatRoomResponse>> aiRoomCreationMap = new ConcurrentHashMap<>();

    /* ================= 채팅방 관리 ================= */

    @Override
    @Transactional
    @CacheEvict(value = "chatRooms", key = "#creatorId")
    public ChatRoomResponse createChatRoom(ChatRoomRequest request, String creatorId) {
        ChatRoom chatRoom = ChatRoom.builder()
                .partyPostNo(request.getPartyPostNo())
                .roomName(request.getRoomName())
                .roomType(request.getRoomType())
                .isActive(true)
                .build();
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 생성자 자동 참가 (배치 처리)
        ChatParticipant participant = ChatParticipant.builder()
                .chatRoomId(savedRoom.getChatRoomId())
                .memberId(creatorId)
                .build();
        chatParticipantRepository.save(participant);

        // 환영 메시지 (비동기 처리)
        createWelcomeMessageAsync(savedRoom.getChatRoomId());

        log.info("채팅방 생성 완료: {} by {}", savedRoom.getChatRoomId(), creatorId);
        return buildChatRoomResponse(savedRoom);
    }

    @Override
    public List<ChatRoomResponse> getMyChatRooms(String memberId) {
        log.info("사용자 {}의 채팅방 목록 조회", memberId);
        
        try {
            // ✅ 수정된 Repository 메서드 사용
            List<ChatRoom> rooms = chatRoomRepository.findByMemberIdWithDetails(memberId);
            
            return rooms.stream()
                    .map(this::buildChatRoomResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("채팅방 목록 조회 실패: memberId={}", memberId, e);
            // 빈 목록 반환 (서비스 중단 방지)
            return List.of();
        }
    }

    @Override
    @Transactional
    public void joinChatRoom(Integer roomId, String memberId) {
        // ✅ 중복 참가 체크 최적화
        if (chatParticipantRepository.existsByChatRoomIdAndMemberId(roomId, memberId)) {
            log.debug("사용자 {}는 이미 채팅방 {}에 참가중", memberId, roomId);
            return;
        }

        ChatParticipant participant = ChatParticipant.builder()
                .chatRoomId(roomId)
                .memberId(memberId)
                .build();
        chatParticipantRepository.save(participant);
        
        // 캐시 무효화
        invalidateUserCache(memberId);
        
        log.info("사용자 {}가 채팅방 {}에 참가", memberId, roomId);
    }

    /* ================= 메시지 관리 ================= */

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageRequest request, String senderId) {
        // ✅ 입력 검증 강화
        validateMessageRequest(request, senderId);

        // ✅ 권한 검증 최적화
        if (!chatParticipantRepository.existsByChatRoomIdAndMemberId(request.getChatRoomId(), senderId)) {
            throw new IllegalStateException("채팅방에 참가하지 않은 사용자입니다");
        }

        // ✅ 발신자 정보 캐시 활용
        String senderNickname = getCachedMemberNickname(senderId);

        // 메시지 저장
        ChatMessage savedMessage = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatRoomId(request.getChatRoomId())
                        .senderId(senderId)
                        .messageText(request.getMessageText())
                        .build()
        );

        // 캐시 무효화
        invalidateMessageCache(request.getChatRoomId());

        return ChatMessageResponse.builder()
                .chatMessagesId(savedMessage.getChatMessagesId())
                .chatRoomId(savedMessage.getChatRoomId())
                .senderId(savedMessage.getSenderId())
                .senderNickname(senderNickname)
                .messageText(savedMessage.getMessageText())
                .sentAt(savedMessage.getSentAt())
                .build();
    }

    public Page<ChatMessageResponse> getChatMessages(Integer roomId, String memberId, Pageable pageable) {
        validateMessageAccess(roomId, memberId);

        // Pageable null 체크
        if (pageable == null) {
            pageable = PageRequest.of(0, 50);
        }

        try {
            Page<ChatMessage> messagePage = chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(roomId, pageable);
            
            return messagePage.map(message -> {
                try {
                    return ChatMessageResponse.builder()
                            .chatMessagesId(message.getChatMessagesId())
                            .chatRoomId(message.getChatRoomId())
                            .senderId(message.getSenderId() != null ? message.getSenderId() : "SYSTEM")
                            .senderNickname(getSenderDisplayName(message))
                            .messageText(message.getMessageText() != null ? message.getMessageText() : "")
                            .sentAt(message.getSentAt() != null ? message.getSentAt() : LocalDateTime.now())
                            .build();
                } catch (Exception e) {
                    log.warn("메시지 변환 실패: messageId={}", message.getChatMessagesId(), e);
                    // 기본값으로 안전한 응답 생성
                    return ChatMessageResponse.builder()
                            .chatMessagesId(message.getChatMessagesId())
                            .chatRoomId(message.getChatRoomId())
                            .senderId("SYSTEM")
                            .senderNickname("시스템")
                            .messageText("메시지 로드 오류")
                            .sentAt(LocalDateTime.now())
                            .build();
                }
            });
        } catch (Exception e) {
            log.error("메시지 조회 실패: roomId={}, memberId={}", roomId, memberId, e);
            return Page.empty(); // 빈 페이지 반환
        }
    }

    /* ================= AI 채팅방 최적화 ================= */

    @Override
    @Transactional
    public ChatRoomResponse getOrCreateAiChatRoom(String memberId) {
        log.info("사용자 {}의 AI 채팅방 조회/생성 요청", memberId);
        
        // ✅ 중복 요청 방지
        return aiRoomCreationMap.computeIfAbsent(memberId, key -> 
            CompletableFuture.supplyAsync(() -> createAiRoomInternal(memberId))
        ).join();
    }

    private ChatRoomResponse createAiRoomInternal(String memberId) {
        try {
            // 기존 AI 채팅방 조회
            Optional<ChatRoom> existingRoom = chatRoomRepository
                    .findByRoomTypeAndMemberId(ChatRoom.RoomType.AI, memberId);
            
            if (existingRoom.isPresent()) {
                log.info("기존 AI 채팅방 발견: {}", existingRoom.get().getChatRoomId());
                return buildChatRoomResponse(existingRoom.get());
            }

            // 새 AI 채팅방 생성
            ChatRoom aiRoom = ChatRoom.builder()
                    .roomName("AI 어시스턴트")
                    .roomType(ChatRoom.RoomType.AI)
                    .isActive(true)
                    .build();
            ChatRoom savedRoom = chatRoomRepository.save(aiRoom);

            // 사용자 자동 참가
            ChatParticipant participant = ChatParticipant.builder()
                    .chatRoomId(savedRoom.getChatRoomId())
                    .memberId(memberId)
                    .joinedAt(LocalDateTime.now())
                    .build();
            chatParticipantRepository.save(participant);

            // 환영 메시지 (비동기)
            createAiWelcomeMessageAsync(savedRoom.getChatRoomId());

            // 캐시 무효화
            invalidateUserCache(memberId);

            log.info("AI 채팅방 생성 완료: {} for {}", savedRoom.getChatRoomId(), memberId);
            return buildChatRoomResponse(savedRoom);
            
        } finally {
            aiRoomCreationMap.remove(memberId);
        }
    }

    @Override
    @Transactional
    public ChatMessageResponse sendAiMessage(String userMessage, String memberId) {
        log.info("AI 메시지 처리: 사용자={}, 메시지길이={}", memberId, userMessage.length());
        
        // AI 채팅방 확인/생성
        ChatRoomResponse aiRoom = getOrCreateAiChatRoom(memberId);

        // ✅ 사용자 메시지 먼저 저장 및 브로드캐스트
        ChatMessageResponse userMessageResponse = saveAndBroadcastUserMessage(aiRoom, userMessage, memberId);

        // ✅ AI 응답 비동기 처리
        processAiResponseAsync(aiRoom, userMessage);

        return userMessageResponse;
    }

    /* ================= 내부 헬퍼 메서드들 ================= */

    private String getCachedMemberNickname(String memberId) {
        // null 체크 추가
        if (memberId == null || memberId.trim().isEmpty()) {
            return "알 수 없는 사용자";
        }
        
        try {
            return memberRepository.findById(memberId)
                    .map(Member::getNickname)
                    .orElse(memberId); // 사용자를 찾지 못하면 ID 자체를 반환
        } catch (Exception e) {
            log.warn("사용자 닉네임 조회 실패: memberId={}", memberId, e);
            return memberId; // 에러 발생시 ID 반환
        }
    }

    private void validateMessageRequest(ChatMessageRequest request, String senderId) {
        if (request.getChatRoomId() == null || senderId == null || 
            request.getMessageText() == null || request.getMessageText().trim().isEmpty()) {
            throw new IllegalArgumentException("필수 입력값이 누락되었습니다");
        }
    }

    private void validateMessageAccess(Integer roomId, String memberId) {
        if (roomId == null || memberId == null) {
            throw new IllegalArgumentException("필수 입력값이 누락되었습니다");
        }
        if (!chatParticipantRepository.existsByChatRoomIdAndMemberId(roomId, memberId)) {
            throw new IllegalStateException("채팅방에 참가하지 않은 사용자입니다");
        }
    }

    private String getSenderDisplayName(ChatMessage message) {
        String senderId = message.getSenderId();
        
        // null 체크 추가
        if (senderId == null || senderId.trim().isEmpty()) {
            return "알 수 없는 사용자";
        }
        
        if ("SYSTEM".equals(senderId)) return "시스템";
        if ("AI_ASSISTANT".equals(senderId)) return "AI 어시스턴트";
        
        return getCachedMemberNickname(senderId);
    }

    private ChatRoomResponse buildChatRoomResponse(ChatRoom chatRoom) {
        long participantCount = chatParticipantRepository.countByChatRoomId(chatRoom.getChatRoomId());
        ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomIdOrderBySentAtDesc(chatRoom.getChatRoomId());

        return ChatRoomResponse.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .partyPostNo(chatRoom.getPartyPostNo())
                .roomName(chatRoom.getRoomName())
                .roomType(chatRoom.getRoomType())
                .createdAt(chatRoom.getCreatedAt())
                .isActive(chatRoom.getIsActive())
                .participantCount(participantCount)
                .lastMessage(lastMessage != null ? lastMessage.getMessageText() : null)
                .lastMessageTime(lastMessage != null ? lastMessage.getSentAt() : null)
                .build();
    }

    private ChatMessageResponse saveAndBroadcastUserMessage(ChatRoomResponse aiRoom, String userMessage, String memberId) {
        // 사용자 메시지 저장
        ChatMessage userChatMessage = chatMessageRepository.save(ChatMessage.builder()
                .chatRoomId(aiRoom.getChatRoomId())
                .senderId(memberId)
                .messageText(userMessage)
                .build());

        String userNickname = getCachedMemberNickname(memberId);

        ChatMessageResponse userMessageResponse = ChatMessageResponse.builder()
                .chatMessagesId(userChatMessage.getChatMessagesId())
                .chatRoomId(userChatMessage.getChatRoomId())
                .senderId(memberId)
                .senderNickname(userNickname)
                .messageText(userChatMessage.getMessageText())
                .sentAt(userChatMessage.getSentAt())
                .build();

        // 즉시 브로드캐스트
        messagingTemplate.convertAndSend(
                "/topic/chat/room/" + aiRoom.getChatRoomId(),
                userMessageResponse
        );

        // 캐시 무효화
        invalidateMessageCache(aiRoom.getChatRoomId());

        return userMessageResponse;
    }

    @Async("aiExecutor")
    public void processAiResponseAsync(ChatRoomResponse aiRoom, String userMessage) {
        try {
            String aiResponse = generateAiResponse(userMessage);

            // ✅ AI 응답 저장 시 sender_id 확실히 설정
            ChatMessage aiMessage = chatMessageRepository.save(ChatMessage.builder()
                    .chatRoomId(aiRoom.getChatRoomId())
                    .senderId("AI_ASSISTANT") // 확실히 설정
                    .messageText(aiResponse != null ? aiResponse : "AI 응답 생성 실패")
                    .sentAt(LocalDateTime.now()) // 시간도 확실히 설정
                    .build());

            ChatMessageResponse aiMessageResponse = ChatMessageResponse.builder()
                    .chatMessagesId(aiMessage.getChatMessagesId())
                    .chatRoomId(aiMessage.getChatRoomId())
                    .senderId("AI_ASSISTANT")
                    .senderNickname("AI 어시스턴트")
                    .messageText(aiResponse != null ? aiResponse : "AI 응답 생성 실패")
                    .sentAt(aiMessage.getSentAt())
                    .build();

            messagingTemplate.convertAndSend(
                    "/topic/chat/room/" + aiRoom.getChatRoomId(),
                    aiMessageResponse
            );

        } catch (Exception e) {
            log.error("AI 응답 처리 실패", e);
            
            // ✅ 에러 발생시에도 시스템 메시지로 알림
            ChatMessage errorMessage = chatMessageRepository.save(ChatMessage.builder()
                    .chatRoomId(aiRoom.getChatRoomId())
                    .senderId("SYSTEM") // 확실한 시스템 ID
                    .messageText("AI 서비스에 일시적인 문제가 발생했습니다.")
                    .sentAt(LocalDateTime.now())
                    .build());
            
            ChatMessageResponse errorResponse = ChatMessageResponse.builder()
                    .chatMessagesId(errorMessage.getChatMessagesId())
                    .chatRoomId(errorMessage.getChatRoomId())
                    .senderId("SYSTEM")
                    .senderNickname("시스템")
                    .messageText("AI 서비스에 일시적인 문제가 발생했습니다.")
                    .sentAt(errorMessage.getSentAt())
                    .build();

            messagingTemplate.convertAndSend(
                    "/topic/chat/room/" + aiRoom.getChatRoomId(),
                    errorResponse
            );
        }
    }

    private String generateAiResponse(String userMessage) {
        try {
            // ✅ 한국어 응답을 명시적으로 요청하는 프롬프트
            String koreanPrompt = String.format(
                "당신은 친근하고 도움이 되는 한국어 AI 어시스턴트입니다. " +
                "사용자의 질문에 반드시 한국어로만 응답해주세요. " +
                "답변은 자연스럽고 이해하기 쉽게, 200자 이내로 간결하게 작성해주세요.\n\n" +
                "사용자 질문: \"%s\"\n\n" +
                "한국어 응답:",
                userMessage
            );
            
            return chatClient.prompt()
                    .user(koreanPrompt)
                    .call()
                    .content();
                    
        } catch (Exception e) {
            log.error("AI 응답 생성 실패", e);
            return "죄송합니다. 현재 AI 서비스에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }

    @Async
    public void createWelcomeMessageAsync(Integer roomId) {
        chatMessageRepository.save(ChatMessage.builder()
                .chatRoomId(roomId)
                .senderId("SYSTEM")
                .messageText("채팅방이 생성되었습니다. 환영합니다!")
                .build());
    }

    @Async
    public void createAiWelcomeMessageAsync(Integer roomId) {
        chatMessageRepository.save(ChatMessage.builder()
                .chatRoomId(roomId)
                .senderId("AI_ASSISTANT")
                .messageText("안녕하세요! AI 어시스턴트입니다. 무엇을 도와드릴까요?")
                .build());
    }

    private void sendErrorMessage(Integer roomId, String message) {
        ChatMessage errorMessage = ChatMessage.builder()
                .chatRoomId(roomId)
                .senderId("SYSTEM")
                .messageText(message)
                .sentAt(LocalDateTime.now())
                .build();
        
        ChatMessageResponse errorResponse = ChatMessageResponse.builder()
                .senderId("SYSTEM")
                .senderNickname("시스템")
                .messageText(message)
                .sentAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, errorResponse);
    }

    // 캐시 무효화 메서드들
    @CacheEvict(value = "chatRooms", key = "#memberId")
    private void invalidateUserCache(String memberId) {
        log.debug("사용자 {} 채팅방 캐시 무효화", memberId);
    }

    @CacheEvict(value = "chatMessages", allEntries = true)
    private void invalidateMessageCache(Integer roomId) {
        log.debug("채팅방 {} 메시지 캐시 무효화", roomId);
    }

    // 나머지 기존 메서드들...
    @Override
    public List<ChatRoomResponse> getAllActiveRooms() {
        return chatRoomRepository.findByIsActiveTrue()
                .stream()
                .map(this::buildChatRoomResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChatRoomResponse getChatRoom(Integer roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다"));
        return buildChatRoomResponse(chatRoom);
    }

    @Override
    @Transactional
    public void leaveChatRoom(Integer roomId, String memberId) {
        chatParticipantRepository.deleteById(new ChatParticipantId(roomId, memberId));
        invalidateUserCache(memberId);
        log.info("사용자 {}가 채팅방 {}에서 퇴장", memberId, roomId);
    }

    @Override
    @Transactional
    public void deleteChatRoom(Integer roomId, String memberId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다"));
        chatRoom.setIsActive(false);
        chatRoomRepository.save(chatRoom);
        invalidateUserCache(memberId);
        log.info("채팅방 {} 비활성화", roomId);
    }

    @Override
    public List<ChatRoomResponse> getChatRooms(String memberId) {
        return getMyChatRooms(memberId); // 기존 로직 재사용
    }
}
