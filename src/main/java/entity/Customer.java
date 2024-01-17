package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Entity;

import javax.persistence.*;
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Entity
@SequenceGenerator(name = "customer_sequence", sequenceName = "customer_sequence", allocationSize = 1 )
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_sequence")
    private Long customerId;
    private String name;
    private String contact;
    private String email;
}
