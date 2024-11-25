package NbbangProject.nbbangProject.service;

import NbbangProject.nbbangProject.entity.UserEntity;
import NbbangProject.nbbangProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    public void sendVerificationCode(String email, String username, String password) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        // 인증 코드 생성 및 이메일 발송
        String code = emailService.generateVerificationCode();
        emailService.sendVerificationEmail(email, code);

        // Redis에 데이터 저장 (5분 동안 유효)
        redisTemplate.opsForValue().set(email, code, 300, TimeUnit.SECONDS); // 인증 코드
        redisTemplate.opsForValue().set(email + ":username", username, 300, TimeUnit.SECONDS); // 사용자 이름
        redisTemplate.opsForValue().set(email + ":password", password, 300, TimeUnit.SECONDS); // 비밀번호
    }

    public boolean verifyCode(String email, String inputCode) {
        String savedCode = redisTemplate.opsForValue().get(email);
        return savedCode != null && savedCode.equals(inputCode);
    }

    public void completeSignup(String email) {
        // Redis에서 데이터 조회
        String username = redisTemplate.opsForValue().get(email + ":username");
        String password = redisTemplate.opsForValue().get(email + ":password");

        if (username == null || password == null) {
            throw new IllegalArgumentException("회원가입 정보가 유효하지 않습니다.");
        }

        // DB에 사용자 저장
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        // Redis 데이터 삭제
        redisTemplate.delete(email);
        redisTemplate.delete(email + ":username");
        redisTemplate.delete(email + ":password");
    }
}
