package com.blog.blogger.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// @Entity
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// @Table(name="users")
// public class User {
//     @Id
//     @GeneratedValue(strategy=GenerationType.IDENTITY)
//     private long id;
//     @Column(nullable= false, unique= true)
//     private String email;
//     @Column(nullable=false)
//     private String username;

//     @Column(nullable=false)
//     private String avatar;

//     @Enumerated(EnumType.STRING)
//     private Role role;

//     @Column(nullable = false)
//     private String password;
    
//     // public User(){
//     //     this.id = id;
//     //     this.username = username;
//     //     this.email = email;
//     //     this.avatar = avatar;
//     //     this.age = age;

//     // }
    
// }


@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    private String bio;
    private String profileImage;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    private boolean enabled = true;
    // Constructors
    public User() {}
    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
    public String getEmail(){
       return  this.email;
    }
    public String getPassword(){
        return this.password;
    }
    public Role getRole(){
        return this.role;
    }
    public void setPassword(String s){
        this.password = s;
    }
    public void setRole(Role r){
        this.role = r;
    }
    
    // public void get
    // Getters & Setters
    // ...
}
