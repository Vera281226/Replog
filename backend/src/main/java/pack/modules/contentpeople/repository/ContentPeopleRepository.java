package pack.modules.contentpeople.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pack.modules.contentpeople.model.ContentPeople;
import pack.modules.contentpeople.model.ContentPeopleId;

import java.util.List;

/**
 * 콘텐츠-인물 매핑에 대한 데이터베이스 접근을 처리하는 Repository 인터페이스입니다.
 */
@Repository
public interface ContentPeopleRepository extends JpaRepository<ContentPeople, ContentPeopleId> {

    /**
     * 특정 콘텐츠에 연결된 인물 목록을 조회합니다.
     *
     * @param contentId 콘텐츠 ID
     * @return 해당 콘텐츠에 매핑된 ContentPeople 리스트
     */
    List<ContentPeople> findByContentId(int contentId);

    /**
     * 특정 인물에 연결된 콘텐츠 목록을 조회합니다.
     *
     * @param personId 인물 ID
     * @return 해당 인물에 매핑된 ContentPeople 리스트
     */
    List<ContentPeople> findByPersonId(int personId);
}
