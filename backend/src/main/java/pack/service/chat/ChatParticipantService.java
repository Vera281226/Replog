package pack.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pack.model.chat.ChatParticipant;
import pack.repository.chat.ChatParticipantRepository;

@Service
@RequiredArgsConstructor
public class ChatParticipantService {
    private final ChatParticipantRepository chatParticipantRepository;

    @Transactional
    public void registerParticipant(Integer chatRoomId, String memberId) {
        if (chatParticipantRepository.existsByChatRoomIdAndMemberId(chatRoomId, memberId)) {
            return; // 중복 방지
        }
        ChatParticipant participant = ChatParticipant.builder()
            .chatRoomId(chatRoomId)
            .memberId(memberId)
            .build();
        chatParticipantRepository.save(participant);
    }

}
