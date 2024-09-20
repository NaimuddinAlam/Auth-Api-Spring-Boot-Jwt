package com.user.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "roles")

public class Role {
    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private  Integer roleId;
    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "role_name")
    private AppRole roleName;
    public Role(AppRole roleName) {
        this.roleName = roleName;
    }
}
