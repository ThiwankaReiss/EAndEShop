package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
@SequenceGenerator(name = "employee_sequence", sequenceName = "employee_sequence", allocationSize = 1 )
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_sequence")
    private Long userId;
    @Column(name = "email", nullable = false)
    private String email;
    private String position;
    @Column(name = "contact", nullable = false)
    private String contact;
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    private String password;
    private String description;


//    public Employee(Long userId, String email, String position, String contact, String name, String password, String description) {
//        this.userId = userId;
//        this.email = email;
//        this.position = position;
//        this.contact = contact;
//        this.name = name;
//        this.password = password;
//        this.description = description;
//    }



}
