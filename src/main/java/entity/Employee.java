package entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@ToString
@Entity
public class Employee {
    @Id
    private String userId;
    private String email;
    private String position;
    private String contact;
    private String name;
    private String password;
    private String description;


    public Employee(String userId, String email, String position, String contact, String name, String password, String description) {
        this.userId = userId;
        this.email = email;
        this.position = position;
        this.contact = contact;
        this.name = name;
        this.password = password;
        this.description = description;
    }



}
