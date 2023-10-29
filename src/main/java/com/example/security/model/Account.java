package com.example.security.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;





    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Account() {
    }
}
