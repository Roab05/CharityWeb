package group3.project.charityweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CharityWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CharityWebApplication.class, args);
    }

}
