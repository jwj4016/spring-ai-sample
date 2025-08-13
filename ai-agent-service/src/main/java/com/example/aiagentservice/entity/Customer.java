package com.example.aiagentservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Customer {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private int age;
	private String gender; // "male", "female"
}
