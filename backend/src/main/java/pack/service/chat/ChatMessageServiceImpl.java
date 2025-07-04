package pack.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pack.dto.chat.ChatMessageRequest;
import pack.dto.chat.ChatMessageResponse;
import pack.model.chat.ChatMessage;
import pack.repository.chat.ChatMessageRepository;
import pack.repository.chat.ChatParticipantRepository;
import pack.repository.member.MemberRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageRequest request, String senderId) {
        if (!chatParticipantRepository.existsByChatRoomIdAndMemberId(request.getChatRoomId(), senderId)) {
            throw new IllegalStateException("채팅방에 참가하지 않은 사용자입니다");
        }
        String senderNickname = memberRepository.findById(senderId)
                .map(m -> m.getNickname())
                .orElse(senderId);

        ChatMessage savedMessage = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatRoomId(request.getChatRoomId())
                        .senderId(senderId)
                        .messageText(request.getMessageText())
                        .build()
        );
        return ChatMessageResponse.builder()
                .chatMessagesId(savedMessage.getChatMessagesId())
                .chatRoomId(savedMessage.getChatRoomId())
                .senderId(savedMessage.getSenderId())
                .senderNickname(senderNickname)
                .messageText(savedMessage.getMessageText())
                .sentAt(savedMessage.getSentAt())
                .build();
    }

    @Override
    public Page<ChatMessageResponse> getChatMessages(Integer roomId, String memberId, Pageable pageable) {
        if (!chatParticipantRepository.existsByChatRoomIdAndMemberId(roomId, memberId)) {
            throw new IllegalStateException("채팅방에 참가하지 않은 사용자입니다");
        }
        if (pageable == null) {
            pageable = PageRequest.of(0, 50);
        }
        Page<ChatMessage> messagePage = chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(roomId, pageable);
        return messagePage.map(message -> ChatMessageResponse.builder()
                .chatMessagesId(message.getChatMessagesId())
                .chatRoomId(message.getChatRoomId())
                .senderId(message.getSenderId() != null ? message.getSenderId() : "SYSTEM")
                .senderNickname(getSenderDisplayName(message))
                .messageText(message.getMessageText() != null ? message.getMessageText() : "")
                .sentAt(message.getSentAt() != null ? message.getSentAt() : LocalDateTime.now())
                .build());
    }

    private String getSenderDisplayName(ChatMessage message) {
        String senderId = message.getSenderId();
        if (senderId == null || senderId.trim().isEmpty()) {
            return "알 수 없는 사용자";
        }
        if ("SYSTEM".equals(senderId)) return "시스템";
        if ("AI_ASSISTANT".equals(senderId)) return "AI 어시스턴트";
        return memberRepository.findById(senderId)
                .map(m -> m.getNickname())
                .orElse(senderId);
    }
}
