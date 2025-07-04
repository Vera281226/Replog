package pack.service.member;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
public class EmailSender {
	
	
	private final JavaMailSender mailSender;
	
	public void send(String to, String subject, String content) {
		MimeMessage message = mailSender.createMimeMessage();  // 이메일 한 통을 만들기 위한 빈 메세지 객체 생성
	
		try {
			// 메세지를 쉽게 구성할 수 있도록 도와주는 도우미 객체
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // true는 HTML 형식 허용
			
			helper.setFrom("frasmoi@naver.com");
			// 수신자 설정
			helper.setTo(to);
			
			// 제목 설정
			helper.setSubject(subject);
			
			// 본문 설정 (HTML 허용)
			helper.setText(content, true);
			
			// 최종적으로 이메일 전송
			mailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("이메일 전송 실패", e);
		}
	}
}
