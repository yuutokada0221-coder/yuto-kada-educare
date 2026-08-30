package jp.educure.problem3.userservice.repository;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    public String findUserData() {
        return "Sample User Data";
    }
}

