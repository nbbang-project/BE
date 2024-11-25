package NbbangProject.nbbangProject.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationDto {
    @NotEmpty(message = "이메일은 필수항목입니다.")
    private String email;

    @NotEmpty(message = "인증 코드는 필수항목입니다.")
    private String code;  // 인증 코드 필드
}
