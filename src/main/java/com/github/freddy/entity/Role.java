package com.github.freddy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tb_roles")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)

    private RoleName authority;

    // O método do Spring Security agora converte o Enum para String
    //@Override
    public String getAuthority() {
        return authority.name(); // .name() transforma "ROLE_ADMIN" em String
    }
}
