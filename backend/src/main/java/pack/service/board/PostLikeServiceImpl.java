package pack.service.board;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pack.model.board.Post;
import pack.model.board.PostLike;
import pack.repository.board.PostLikeRepository;
import pack.repository.board.PostRepository;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public boolean likePost(String id, Integer postNo) {
        Post post = postRepository.findById(postNo)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        boolean alreadyLiked = postLikeRepository.existsByIdAndPostNo(id, postNo);
        if (alreadyLiked) {
            postLikeRepository.deleteByIdAndPostNo(id, postNo);
            post.decreaseLikes(); // 👍 좋아요 감소
        } else {
            postLikeRepository.save(PostLike.builder().id(id).postNo(postNo).build());
            post.increaseLikes(); // 👍 좋아요 증가
        }

        // 변경 감지(dirty checking)로 save 생략 가능하지만 명시적으로 저장할 수도 있음
        postRepository.save(post);
        return !alreadyLiked;
    }

    @Override
    public boolean isPostLiked(String id, Integer postNo) {
        return postLikeRepository.existsByIdAndPostNo(id, postNo);
    }

    @Override
    @Transactional
    public void cancelLike(String id, Integer postNo) {
        Post post = postRepository.findById(postNo)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        boolean alreadyLiked = postLikeRepository.existsByIdAndPostNo(id, postNo);
        if (alreadyLiked) {
            postLikeRepository.deleteByIdAndPostNo(id, postNo);
            post.decreaseLikes(); // 👍 좋아요 감소
            postRepository.save(post);
        }
    }
}