package com.crm.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "leads")
public class Lead extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "email", unique = false)
    private String email;

    @Column(name = "phone_number", unique = false)
    private String phoneNumber;

    @Column(name = "company")
    private String company;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "expected_revenue")
    private BigDecimal expectedRevenue;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @ManyToOne
    @JoinColumn(name = "stage", nullable = false)
    private Stage stage;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL)
    private List<Quotation> quotations;

}
