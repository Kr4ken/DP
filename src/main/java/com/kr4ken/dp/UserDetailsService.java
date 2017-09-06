package com.kr4ken.dp;

public interface UserDetailsService {
    org.springframework.security.core.userdetails.UserDetails loadUserByUsername(java.lang.String s)
            throws org.springframework.security.core.userdetails.UsernameNotFoundException;

}
