package com.pet.adoption.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;
    private String phone;

    @Column(columnDefinition = "varchar(20) default 'USER'")
    private String role = "USER";

    @Column(columnDefinition = "varchar(20) default 'NORMAL'")
    private String status = "NORMAL";
}