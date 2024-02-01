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
@SequenceGenerator(name = "order_sequence", sequenceName = "order_sequence", allocationSize = 1 )
public class Orders {
//    @ManyToOne
//    @JoinColumn(name ="customer_id" )
//    private Customer customer;
//    @OneToMany(mappedBy = "orders")
//    private List<OrderDetail> orderDetails=new ArrayList<>();

//    public Orders(Long orderId, Long customerId,Long itemId, String orderStaus, double fee,String date) {
//        this.customerId = customerId;
//        this.orderId = orderId;
//        this.itemId=itemId;
//        this.orderStaus = orderStaus;
//        this.fee = fee;
//        this.date=date;
//    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_sequence")
    private Long orderId;
    private Long customerId;
    private Long employeeId;
    private Long itemId;
    private String orderStaus;
    private double fee;
    private String date;
    private double total;
}
