package entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
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


    public Employee(String userId, String email, String position, String contact, String name, String password) {
        this.userId = userId;
        this.email = email;
        this.position = position;
        this.contact = contact;
        this.name = name;
        this.password = password;
    }


}
