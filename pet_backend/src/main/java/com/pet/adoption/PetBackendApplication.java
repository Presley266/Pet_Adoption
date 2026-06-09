package com.pet.adoption;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetBackendApplication.class, args);
        System.out.println("宠物领养系统启动成功！ http://localhost:5173/");
    }
}