package NbbangProject.nbbangProject.controller;

import NbbangProject.nbbangProject.form.UserCreateForm;
import NbbangProject.nbbangProject.service.UserService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;


    @GetMapping("/signup")
    public ResponseEntity<String> signup() {
        // 클라이언트가 GET 요청을 보낼 경우 처리할 로직 (예: signup 폼을 요청)
        return ResponseEntity.ok("회원가입 페이지 요청");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("유효성 검사 실패: 입력 값을 확인해주세요.");
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }

        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(),
                    userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 에러: " + e.getMessage());
        }

        return ResponseEntity.ok("회원가입 성공");
    }

}