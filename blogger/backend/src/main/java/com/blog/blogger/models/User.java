package com.blog.blogger.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data  // من Lombok: كتدير getters/setters/toString/equals/hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // باش نعرف واش user عادي ولا admin
    @Column(nullable = false)
    private String role = "USER";
}
