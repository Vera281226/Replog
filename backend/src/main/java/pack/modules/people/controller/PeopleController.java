package pack.modules.people.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pack.modules.people.dto.PeopleRequest;
import pack.modules.people.dto.PeopleResponse;
import pack.modules.people.service.PeopleService;

/**
 * PeopleController
 * <p>
 * 인물 관련 API 요청을 처리하는 REST 컨트롤러입니다.
 * <p>
 * 기본 경로: /api/people
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/people")
public class PeopleController {

    private final PeopleService peopleService;

    /**
     * 전체 인물 목록을 조회합니다.
     *
     * @return 인물 목록 응답 리스트
     */
    @GetMapping
    public ResponseEntity<List<PeopleResponse>> getAllPeople() {
        return ResponseEntity.ok(peopleService.getAllPeople());
    }

    /**
     * 특정 인물 정보를 조회합니다.
     *
     * @param id 조회할 인물의 ID
     * @return 해당 인물 정보 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<PeopleResponse> getPeople(@PathVariable int id) {
        return ResponseEntity.ok(peopleService.getPeopleById(id));
    }

    /**
     * 새로운 인물 정보를 등록합니다.
     *
     * @param request 등록할 인물 요청 데이터
     * @return HTTP 200 OK
     */
    @PostMapping
    public ResponseEntity<Void> createPeople(@RequestBody PeopleRequest request) {
        peopleService.createPeople(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 기존 인물 정보를 수정합니다.
     *
     * @param id 수정할 인물 ID
     * @param request 수정할 인물 요청 데이터
     * @return HTTP 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePeople(@PathVariable int id, @RequestBody PeopleRequest request) {
        peopleService.updatePeople(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 인물 정보를 삭제합니다.
     *
     * @param id 삭제할 인물 ID
     * @return HTTP 200 OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePeople(@PathVariable int id) {
        peopleService.deletePeople(id);
        return ResponseEntity.ok().build();
    }
}
