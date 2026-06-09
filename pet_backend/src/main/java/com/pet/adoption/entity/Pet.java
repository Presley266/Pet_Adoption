package com.pet.adoption.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String category;
    private String breed;
    private Integer age;
    private String gender;    // MALE, FEMALE
    private String description;
    private String healthStatus;
    private String imageUrl;

    @Column(columnDefinition = "varchar(20) default 'AVAILABLE'")
    private String status = "AVAILABLE";

    @Column(columnDefinition = "int default 0")
    private Integer favoriteCount = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Date createTime;
}