package jp.educure.problem3.exceptionhandling;

import org.springframework.stereotype.Service;

@Service
public class UsermanagementService {

    private final UserRepository userRepository;

    public UsermanagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(String name, String email) {
        userRepository.save(new User(name, email));
    }
}
