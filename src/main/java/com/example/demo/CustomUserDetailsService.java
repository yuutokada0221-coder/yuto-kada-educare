package com.example.demo;

import com.example.demo.entity.UserAccount;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserAccountRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // データベースからユーザーを探す
        UserAccount userAccount = userRepository.findByUsername(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("ユーザーが見つかりません");
        }
        
        // 見つかったら、Spring Security用のデータに変換して返す（役割はDBのroleに従う）
        String role = userAccount.getRole() != null ? userAccount.getRole() : "USER";
        return User.withUsername(userAccount.getUsername())
                .password(userAccount.getPassword())
                .roles(role)
                .build();
    }
}
