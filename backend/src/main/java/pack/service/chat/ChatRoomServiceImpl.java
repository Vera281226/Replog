package pack.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pack.dto.chat.ChatRoomRequest;
import pack.dto.chat.ChatRoomResponse;
import pack.model.chat.ChatParticipant;
import pack.model.chat.ChatParticipantId;
import pack.model.chat.ChatRoom;
import pack.repository.chat.ChatParticipantRepository;
import pack.repository.chat.ChatRoomRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;

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

        ChatParticipant participant = ChatParticipant.builder()
                .chatRoomId(savedRoom.getChatRoomId())
                .memberId(creatorId)
                .build();
        chatParticipantRepository.save(participant);

        return buildChatRoomResponse(savedRoom);
    }

    @Override
    public List<ChatRoomResponse> getMyChatRooms(String memberId) {
        List<ChatRoom> rooms = chatRoomRepository.findByMemberIdWithDetails(memberId);
        return rooms.stream()
                .map(this::buildChatRoomResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void joinChatRoom(Integer roomId, String memberId) {
        if (chatParticipantRepository.existsByChatRoomIdAndMemberId(roomId, memberId)) {
            return;
        }
        ChatParticipant participant = ChatParticipant.builder()
                .chatRoomId(roomId)
                .memberId(memberId)
                .build();
        chatParticipantRepository.save(participant);
    }

    @Override
    @Transactional
    public void leaveChatRoom(Integer roomId, String memberId) {
        chatParticipantRepository.deleteById(new ChatParticipantId(roomId, memberId));
    }

    @Override
    @Transactional
    public void deleteChatRoom(Integer roomId, String memberId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다"));
        chatRoom.setIsActive(false);
        chatRoomRepository.save(chatRoom);
    }

    @Override
    public ChatRoomResponse getChatRoom(Integer roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다"));
        return buildChatRoomResponse(chatRoom);
    }

    @Override
    public List<ChatRoomResponse> getAllActiveRooms() {
        return chatRoomRepository.findByIsActiveTrue()
                .stream()
                .map(this::buildChatRoomResponse)
                .collect(Collectors.toList());
    }

    private ChatRoomResponse buildChatRoomResponse(ChatRoom chatRoom) {
        long participantCount = chatParticipantRepository.countByChatRoomId(chatRoom.getChatRoomId());
        return ChatRoomResponse.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .partyPostNo(chatRoom.getPartyPostNo())
                .roomName(chatRoom.getRoomName())
                .roomType(chatRoom.getRoomType())
                .createdAt(chatRoom.getCreatedAt())
                .isActive(chatRoom.getIsActive())
                .participantCount(participantCount)
                .build();
    }
}
