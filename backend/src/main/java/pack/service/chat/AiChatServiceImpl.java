package pack.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pack.dto.chat.ChatRoomResponse;
import pack.dto.chat.ChatMessageResponse;
import pack.model.chat.ChatMessage;
import pack.model.chat.ChatParticipant;
import pack.model.chat.ChatRoom;
import pack.model.member.Member;
import pack.modules.contents.model.Contents;
import pack.modules.contents.repository.ContentsRepository;
import pack.repository.chat.ChatMessageRepository;
import pack.repository.chat.ChatParticipantRepository;
import pack.repository.chat.ChatRoomRepository;
import pack.repository.member.MemberRepository;
import pack.util.AuthUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)

public class AiChatServiceImpl implements AiChatService {
	private final ContentsRepository contentsRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatClient chatClient;
    private final ConcurrentHashMap<String, CompletableFuture<ChatRoomResponse>> aiRoomCreationMap = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public ChatRoomResponse getOrCreateAiChatRoom(String memberId) {
        return aiRoomCreationMap.computeIfAbsent(memberId, key ->
                CompletableFuture.supplyAsync(() -> createAiRoomInternal(memberId))
        ).join();
    }

    private ChatRoomResponse createAiRoomInternal(String memberId) {
        try {
            Optional<ChatRoom> existingRoom = chatRoomRepository
                    .findByRoomTypeAndMemberId(ChatRoom.RoomType.AI, memberId);

            if (existingRoom.isPresent()) {
                return buildChatRoomResponse(existingRoom.get());
            }

            ChatRoom aiRoom = ChatRoom.builder()
                    .roomName("AI 어시스턴트")
                    .roomType(ChatRoom.RoomType.AI)
                    .isActive(true)
                    .build();
            ChatRoom savedRoom = chatRoomRepository.save(aiRoom);

            ChatParticipant participant = ChatParticipant.builder()
                    .chatRoomId(savedRoom.getChatRoomId())
                    .memberId(memberId)
                    .joinedAt(LocalDateTime.now())
                    .build();
            chatParticipantRepository.save(participant);

            createAiWelcomeMessageAsync(savedRoom.getChatRoomId());

            return buildChatRoomResponse(savedRoom);
        } finally {
            aiRoomCreationMap.remove(memberId);
        }
    }

    @Override
    @Transactional
    public ChatMessageResponse sendAiMessage(String userMessage, String memberId) {
        ChatRoomResponse aiRoom = getOrCreateAiChatRoom(memberId);
        ChatMessageResponse userMessageResponse = saveAndBroadcastUserMessage(aiRoom, userMessage, memberId);
        processAiResponseAsync(aiRoom, userMessage);
        return userMessageResponse;
    }

    private ChatRoomResponse buildChatRoomResponse(ChatRoom chatRoom) {
        long participantCount = chatParticipantRepository.countByChatRoomId(chatRoom.getChatRoomId());
        return ChatRoomResponse.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .roomName(chatRoom.getRoomName())
                .roomType(chatRoom.getRoomType())
                .createdAt(chatRoom.getCreatedAt())
                .isActive(chatRoom.getIsActive())
                .participantCount(participantCount)
                .build();
    }

    private ChatMessageResponse saveAndBroadcastUserMessage(ChatRoomResponse aiRoom, String userMessage, String memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(aiRoom.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        Member user = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("사용자 계정이 존재하지 않습니다."));

        ChatMessage userChatMessage = chatMessageRepository.save(ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(user)
                .messageText(userMessage)
                .build());

        String userNickname = user.getNickname();

        ChatMessageResponse userMessageResponse = ChatMessageResponse.builder()
                .chatMessagesId(userChatMessage.getChatMessagesId())
                .chatRoomId(userChatMessage.getChatRoom().getChatRoomId())
                .senderId(user.getMemberId())
                .senderNickname(userNickname)
                .messageText(userChatMessage.getMessageText())
                .sentAt(userChatMessage.getSentAt())
                .build();

        messagingTemplate.convertAndSend(
                "/topic/chat/room/" + aiRoom.getChatRoomId(),
                userMessageResponse
        );
        return userMessageResponse;
    }

    @Async("aiExecutor")
    public void processAiResponseAsync(ChatRoomResponse aiRoom, String userMessage) {
        try {
            String aiResponse = generateAiResponse(userMessage);

            ChatRoom chatRoom = chatRoomRepository.findById(aiRoom.getChatRoomId())
                    .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
            Member aiMember = memberRepository.findById("AI_ASSISTANT")
                    .orElseThrow(() -> new RuntimeException("AI 어시스턴트 계정이 존재하지 않습니다."));

            ChatMessage aiMessage = chatMessageRepository.save(ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .sender(aiMember)
                    .messageText(aiResponse != null ? aiResponse : "AI 응답 생성 실패")
                    .sentAt(LocalDateTime.now())
                    .build());

            ChatMessageResponse aiMessageResponse = ChatMessageResponse.builder()
                    .chatMessagesId(aiMessage.getChatMessagesId())
                    .chatRoomId(aiMessage.getChatRoom().getChatRoomId())
                    .senderId(aiMember.getMemberId())
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

            ChatRoom chatRoom = chatRoomRepository.findById(aiRoom.getChatRoomId())
                    .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
            Member systemMember = memberRepository.findById("SYSTEM")
                    .orElseThrow(() -> new RuntimeException("SYSTEM 계정이 존재하지 않습니다."));

            ChatMessage errorMessage = chatMessageRepository.save(ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .sender(systemMember)
                    .messageText("AI 서비스에 일시적인 문제가 발생했습니다.")
                    .sentAt(LocalDateTime.now())
                    .build());

            ChatMessageResponse errorResponse = ChatMessageResponse.builder()
                    .chatMessagesId(errorMessage.getChatMessagesId())
                    .chatRoomId(errorMessage.getChatRoom().getChatRoomId())
                    .senderId(systemMember.getMemberId())
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
            String memberId = AuthUtil.getCurrentMemberId();
            String contentContext = buildContentContext(memberId);

            String koreanPrompt = String.format(
                "당신은 친근하고 도움이 되는 한국어 AI 어시스턴트로 이름은 리플입니다. 본인의 성격이나 기본 응답 설정에 대한 언급 자체는 금지합니다." +
                "사용자의 질문에 무조건 한국어로만 응답해주세요. 사용자의 일반적인 질문이나 농담에는 상황에 맞는 자연스러운 응답을 해주세요." +
                "밑의 영화 추천은 무시하고 사용자와의 대화를 해주세요" +
                "답변은 자연스럽고 이해하기 쉽게, 200자 이내로 간결하고 자연스럽게 작성해주세요.\n\n" +
                "자신에 대한 설명은 이름인 리플 정도와 간략한 인사만 해주시고 영화 추천을 해드릴까요 하는 친근하게 자연스러운 말을 해주세요" +
                "아래는 최근 인기 및 최신 컨텐츠, 그리고 회원님의 관심 장르 추천 컨텐츠 목록입니다.\n" +
                "%s\n" +
                "사용자 질문: \"%s\"\n\n" +
                "한국어 응답:",
                contentContext, userMessage
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
    public void createAiWelcomeMessageAsync(Integer roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        Member aiMember = memberRepository.findById("AI_ASSISTANT")
                .orElseThrow(() -> new RuntimeException("AI 어시스턴트 계정이 존재하지 않습니다."));

        chatMessageRepository.save(ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(aiMember)
                .messageText("안녕하세요! AI 어시스턴트입니다. 무엇을 도와드릴까요?")
                .build());
    }
    
    private String buildContentContext(String memberId) {
        List<Contents> latest = contentsRepository.findTop5ByOrderByReleaseDateDesc();
        List<Contents> popular = contentsRepository.findTop5ByOrderByRatingDesc();

        StringBuilder sb = new StringBuilder();
        sb.append("최신 컨텐츠: ");
        latest.forEach(c -> sb.append(c.getTitle()).append(", "));
        sb.append("\n인기 컨텐츠: ");
        popular.forEach(c -> sb.append(c.getTitle()).append(", "));
        return sb.toString();
    }
}
