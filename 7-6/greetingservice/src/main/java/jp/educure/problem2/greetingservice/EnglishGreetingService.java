package jp.educure.problem2.greetingservice;

import org.springframework.stereotype.Service;

@Service
public class EnglishGreetingService implements GreetingService {

    @Override
    public String greet() {
        return "Hello!";
    }
}
