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
@SequenceGenerator(name = "customer_sequence", sequenceName = "customer_sequence", allocationSize = 1 )
public class Customer {
//    public Customer(Long customerId, String name, String contact, String email) {
//        this.customerId = customerId;
//        this.name = name;
//        this.contact = contact;
//        this.email = email;
//    }
//    @OneToMany(mappedBy ="customer")
//    private List<Orders> ordersList=new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_sequence")
    private Long customerId;
    private String name;
    private String contact;
    private String email;
}
