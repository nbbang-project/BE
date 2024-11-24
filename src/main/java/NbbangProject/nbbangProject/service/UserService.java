package NbbangProject.nbbangProject.service;

import NbbangProject.nbbangProject.entity.UserEntity;
import NbbangProject.nbbangProject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity create(String username, String email, String password) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 등록된 사용자 이름입니다");
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }
}
