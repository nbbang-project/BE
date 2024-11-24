package NbbangProject.nbbangProject.repository;

import NbbangProject.nbbangProject.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <UserEntity, Long> {

    // 이메일로 사용자 존재 여부 확인
    boolean existsByEmail(String email);

    // 사용자 이름으로 사용자 존재 여부 확인
    boolean existsByUsername(String username);

}