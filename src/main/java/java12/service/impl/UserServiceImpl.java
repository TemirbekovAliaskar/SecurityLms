package java12.service.impl;

import jakarta.transaction.Transactional;
import java12.dto.request.UserRequest;
import java12.dto.response.HTTPResponse;
import java12.dto.response.UserResponse;
import java12.entity.Company;
import java12.entity.User;
import java12.repository.CompanyRepository;
import java12.repository.UserRepository;
import java12.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public HTTPResponse signRegister(UserRequest userRequest) {
       boolean exist = userRepo.existsByEmail(userRequest.email());
       if (exist) throw  new RuntimeException("Email : " + userRequest.email() +"already exist!");
       if (!userRequest.password().equals(userRequest.passwordConfirm())) throw new RuntimeException("Invalid password !");

       User user = new User();
       user.setName(userRequest.name());
       user.setEmail(userRequest.email());
       user.setRole(userRequest.role());
       user.setPassword(passwordEncoder.encode(userRequest.password()));
       userRepo.save(user);
        return HTTPResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Successfully saved !")
                .build();
    }

    @Override
    public UserResponse signIn(UserResponse userResponse) {
        User user = userRepo.findByEmail(userResponse.email()).orElseThrow(() ->    new NoSuchElementException("User with email: " + userResponse.email()+" not found!"));

        String encodePassword = user.getPassword();
        String password = userResponse.password();

        boolean matches = passwordEncoder.matches(password, encodePassword);

        if (!matches) throw new RuntimeException("Invalid password");


        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override @Transactional
    public HTTPResponse asSign(Long userId, Long companyId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new NoSuchElementException("Not found Id" + userId));
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new NoSuchElementException("Not found Id" + companyId));
        user.addCompany(company);
        company.addUser(user);
        return HTTPResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Successfully asSigned !")
                .build();
    }
}
