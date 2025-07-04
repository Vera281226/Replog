package pack.common;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.springframework.util.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@Component
public class FileStore {

    /* application.properties:
       app.profile.upload-dir=./uploads/profile/   (기본값) */
    @Value("${app.profile.upload-dir:./uploads/profile/}")
    private String uploadDir;

    /**
     * 프로필 이미지를 디스크에 저장하고
     *  브라우저에서 접근할 수 있는 URL(/uploads/profile/uuid.png) 반환
     */
    public String saveProfileImage(MultipartFile file) {
    	
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일입니다.");
        }

        try {
            /* 1. 저장 디렉터리 확보 & 없으면 생성 */
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            /* 2. 파일명 UUID + 확장자 */
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String storeName = UUID.randomUUID() + (ext != null ? "." + ext : "");
            Path target = dir.resolve(storeName);

            /* 3. 실제 파일 저장 */
            file.transferTo(target);

            /* 4. 웹에서 사용할 상대경로 반환 */
            return "/uploads/profile/" + storeName;

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }
}