package org.example.apionlinebanking.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;


@Entity
@Data
@Table(name = "accounts")
public class Accounts {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_users")
    private long idUsers;

    @Column(name = "money")
    private BigDecimal money;
}
