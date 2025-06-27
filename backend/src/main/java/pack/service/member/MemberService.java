package pack.service.member;

import pack.dto.member.*;

public interface MemberService {
	void registerMember(MemberRequest request);
    void updateMember(MemberRequest request);
    void changePassword(PasswordChangeRequest request);
    void sendPasswordResetCode(String email);
    void resetPassword(PasswordResetRequest request);
    boolean existsByEmail(String email);
    String resetPassword(String email);
    void withdraw(WithdrawRequest request);
    void updateGenres(GenreUpdateRequest request);

}
