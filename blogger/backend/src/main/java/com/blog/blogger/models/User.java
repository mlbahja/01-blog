package com.blog.blogger.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(nullable= false, unique=false)
    private String avatar;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // باش نعرف واش user عادي ولا admin
    @Column(nullable = false)
    private String role;
    

    //getter and setters
    public Long getId(){
        return  this.id;
    }
    public String getAvatar(){
        return this.avatar;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPassword(){
        return this.password;
    }
    public  String getRole(){
        return this.role;
    }
    public void setId(Long i){
        this.id = i;
    }
    public void setUsername(String s){
        this.username = s;
    }
    public void setEmail(String e){
        this.email = e;
    }
    public void setPassword(String s){
        this.password = s;
    }
    public void setRole(String s){
        this.role = s;
    }

}
