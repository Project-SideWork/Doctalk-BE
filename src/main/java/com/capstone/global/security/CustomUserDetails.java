package com.capstone.global.security;


import com.capstone.domain.project.entity.Project;
import com.capstone.domain.task.entity.Task;
import com.capstone.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CustomUserDetails implements UserDetails {

    private User user;

    public String getEmail() {
        return user.getEmail();
    }

    // TODO: 추후 깃허브 계정 정보랑 연동 필요.
    public Long getGithubId() {
        return 1L;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();


        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_VIEWER"));
        }


        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}