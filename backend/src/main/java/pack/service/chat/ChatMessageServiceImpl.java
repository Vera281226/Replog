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
import pack.model.chat.ChatRoom;
import pack.model.member.Member;
import pack.repository.chat.ChatMessageRepository;
import pack.repository.chat.ChatParticipantRepository;
import pack.repository.chat.ChatRoomRepository;
import pack.repository.member.MemberRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageRequest request, String senderId) {
        if (!chatParticipantRepository.existsByChatRoomIdAndMemberId(request.getChatRoomId(), senderId)) {
            throw new IllegalStateException("채팅방에 참가하지 않은 사용자입니다");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
            .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        Member sender = memberRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        ChatMessage savedMessage = chatMessageRepository.save(
            ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .messageText(request.getMessageText())
                .build()
        );
        return ChatMessageResponse.builder()
                .chatMessagesId(savedMessage.getChatMessagesId())
                .chatRoomId(savedMessage.getChatRoom().getChatRoomId())
                .senderId(savedMessage.getSender().getMemberId())
                .senderNickname(savedMessage.getSender().getNickname())
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
        Page<ChatMessage> messagePage = chatMessageRepository.findByChatRoom_ChatRoomIdOrderBySentAtAsc(roomId, pageable);
        return messagePage.map(message -> ChatMessageResponse.builder()
        	    .chatMessagesId(message.getChatMessagesId())
        	    .chatRoomId(message.getChatRoom().getChatRoomId())
        	    .senderId(message.getSender().getMemberId() != null ? message.getSender().getMemberId() : "SYSTEM")
        	    .senderNickname(getSenderDisplayName(message))
        	    .messageText(message.getMessageText() != null ? message.getMessageText() : "")
        	    .sentAt(message.getSentAt() != null ? message.getSentAt() : LocalDateTime.now())
        	    .build());
    }

    private String getSenderDisplayName(ChatMessage message) {
        String senderId = message.getSender().getMemberId();
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
