package pack.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pack.model.member.Genre;
import pack.repository.member.GenreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public List<String> getAllGenreNames() {
        return genreRepository.findAll()
                .stream()
                .map(Genre::getName)
                .collect(Collectors.toList());
    }
}
