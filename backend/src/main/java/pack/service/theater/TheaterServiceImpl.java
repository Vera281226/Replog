package pack.service.theater;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pack.dto.theater.TheaterResponse;
import pack.model.theater.Theater;
import pack.repository.theater.TheaterRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheaterServiceImpl implements TheaterService {

    private final TheaterRepository theaterRepository;

    @Override
    public List<TheaterResponse> getAllTheaters() {
        List<Theater> theaters = theaterRepository.findAll();

        if (theaters.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "등록된 영화관이 없습니다.");
        }

        return theaters.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TheaterResponse> getTheatersByIds(List<Integer> ids) {
        List<Theater> theaters = theaterRepository.findAllById(ids);

        if (theaters.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "선택한 ID에 해당하는 영화관이 없습니다.");
        }

        return theaters.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TheaterResponse toDto(Theater theater) {
        return TheaterResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .address(theater.getAddress())
                .latitude(theater.getLatitude())
                .longitude(theater.getLongitude())
                .build();
    }
}