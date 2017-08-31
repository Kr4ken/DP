package com.kr4ken.dp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(AccountRepository accountRepository,
                           InterestRepository interestRepository,
                           InterestTypeRepository interestTypeRepository
                           ) {
        return (evt) -> Arrays.asList(
                "jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","))
                .forEach(
                        a -> {
                            Account account = accountRepository.save(new Account(a,
                                    "password"));
                            InterestType type1 = interestTypeRepository.save(new InterestType("Type1" + a,"Type 1"));
                            interestRepository.save(new Interest(account,
                                    a+" interest 1", type1));
                            interestRepository.save(new Interest(account,
                                    a+" interest 2", type1));
                        });
    }

}
