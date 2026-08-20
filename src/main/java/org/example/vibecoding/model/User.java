package org.example.vibecoding.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String ref;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}