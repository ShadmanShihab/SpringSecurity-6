package com.eazybytes.springsecsection1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    private String email;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String pwd;

    private String role;

//    @JsonIgnore
//    private Date createDt;
}
