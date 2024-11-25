package NbbangProject.nbbangProject.controller;

import NbbangProject.nbbangProject.dto.UserCreateDto;
import NbbangProject.nbbangProject.dto.VerificationDto;
import NbbangProject.nbbangProject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserCreateDto userCreateDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("입력값이 유효하지 않습니다.");
        }

        if (!userCreateDto.getPassword1().equals(userCreateDto.getPassword2())) {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }

        // 이메일 인증 코드 전송
        try {
            userService.sendVerificationCode(
                    userCreateDto.getEmail(),
                    userCreateDto.getUsername(),
                    userCreateDto.getPassword1()
            );
            return ResponseEntity.ok("이메일로 인증 코드가 발송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signup/code")
    public ResponseEntity<?> verifyCode(@RequestBody VerificationDto verificationRequest) {
        String email = verificationRequest.getEmail();
        String code = verificationRequest.getCode();

        // 인증 코드 확인
        if (userService.verifyCode(email, code)) {
            try {
                userService.completeSignup(email);
                return ResponseEntity.ok("회원가입 성공");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("인증 코드가 잘못되었습니다.");
        }
    }

}
