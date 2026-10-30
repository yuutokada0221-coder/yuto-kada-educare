package jp.educure.problem3.exceptionhandling;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public void save(User user) {
        if ("error@example.com".equals(user.getEmail())) {
            throw new RuntimeException("Error: Unable to save user with email " + user.getEmail());
        }
        users.add(user);
        System.out.println("User saved: " + user.getName() + ", " + user.getEmail());
    }
}
