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
import pack.model.theater.PartyPost;
import pack.repository.chat.ChatParticipantRepository;
import pack.repository.chat.ChatRoomRepository;
import pack.repository.theater.PartyPostRepository;
import pack.service.theater.PartyPostDeleteService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatParticipantService chatParticipantService;
    private final PartyPostRepository partyPostRepository;
    
    @Override
    @Transactional
    @CacheEvict(value = "chatRooms", key = "#p1")
    public ChatRoomResponse createChatRoom(ChatRoomRequest request, String creatorId) {
        PartyPost partyPost = partyPostRepository.findById(request.getPartyPostNo())
            .orElseThrow(() -> new RuntimeException("모집글을 찾을 수 없습니다."));

        if (chatRoomRepository.findByPartyPost_PartyPostNo(request.getPartyPostNo()).isPresent()) {
            throw new IllegalStateException("이미 채팅방이 생성된 모집글입니다.");
        }

        ChatRoom chatRoom = ChatRoom.builder()
            .partyPost(partyPost)
            .roomName(request.getRoomName())
            .roomType(request.getRoomType())
            .isActive(true)
            .build();
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 중복 참가자 insert 방지
        chatParticipantService.registerParticipant(savedRoom.getChatRoomId(), creatorId);

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
    public boolean joinChatRoom(Integer roomId, String memberId) {
        if (chatParticipantRepository.existsByChatRoomIdAndMemberId(roomId, memberId)) {
            return false; // 이미 참가자
        }
        ChatParticipant participant = ChatParticipant.builder()
                .chatRoomId(roomId)
                .memberId(memberId)
                .build();
        chatParticipantRepository.save(participant);
        return true; // 신규 참가
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
    public ChatRoomResponse getRoomByPartyPostNo(Integer partyPostNo) {
        ChatRoom chatRoom = chatRoomRepository.findByPartyPost_PartyPostNo(partyPostNo)
            .orElseThrow(() -> new RuntimeException("해당 모집글에 연동된 채팅방이 없습니다."));
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
        Long participantCount = chatParticipantRepository.countByChatRoomId(chatRoom.getChatRoomId());
        Integer partyPostNo = chatRoom.getPartyPost() != null ? chatRoom.getPartyPost().getPartyPostNo() : null;

        return ChatRoomResponse.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .partyPostNo(partyPostNo)
                .roomName(chatRoom.getRoomName())
                .roomType(chatRoom.getRoomType())
                .createdAt(chatRoom.getCreatedAt())
                .isActive(chatRoom.getIsActive())
                .participantCount(participantCount)
                .build();
    }

    @Override
    @Transactional
    public void removeParticipant(Integer roomId, String memberId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다"));
        
        // 참가자 제거
        chatParticipantRepository.deleteById(new ChatParticipantId(roomId, memberId));
        
        // 모집글 작성자인지 확인
        if (chatRoom.getPartyPost() != null) {
            String postAuthor = chatRoom.getPartyPost().getMemberId();
            if (postAuthor.equals(memberId)) {
                // 모집글 작성자가 나가는 경우 - 모든 참가자 제거 후 방 비활성화
                chatParticipantRepository.deleteAllByChatRoomId(roomId);
                chatRoom.setIsActive(false);
                chatRoomRepository.save(chatRoom);
            }
        }
    }
    
    
    
    @Override
    @Transactional
    public void kickAllParticipants(Integer roomId) {
        chatParticipantRepository.deleteAllByChatRoomId(roomId);
    }
}
