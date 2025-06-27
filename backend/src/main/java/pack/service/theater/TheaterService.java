package pack.service.theater;

import pack.dto.theater.TheaterResponse;

import java.util.List;

public interface TheaterService {
    List<TheaterResponse> getAllTheaters();
    List<TheaterResponse> getTheatersByIds(List<Integer> ids);
}