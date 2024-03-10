package java12.repository;

import java12.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserRepository extends JpaRepository<User, Long> {



    @Query("select u from User u where  u.email = :email")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}