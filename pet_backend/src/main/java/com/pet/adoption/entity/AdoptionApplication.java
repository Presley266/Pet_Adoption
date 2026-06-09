package com.pet.adoption.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "adoption_application")
public class AdoptionApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;
    private Integer petId;
    private String applyReason;
    private String applicantName;
    private String applicantPhone;
    private String applicantAddress;

    @Column(columnDefinition = "varchar(20) default 'PENDING'")
    private String status = "PENDING";  // PENDING, APPROVED, REJECTED, CANCELLED

    private String reviewComment;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Date createTime;

    private Date reviewTime;
}