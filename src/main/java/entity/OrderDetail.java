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
@SequenceGenerator(name = "orderDetail_sequence", sequenceName = "orderDetail_sequence", allocationSize = 1 )
public class OrderDetail {

//    @EmbeddedId
//    private OrderDetailKey id;
//
//    @ManyToOne
//    @MapsId("partId")
//    @JoinColumn(name = "part_Id")
//    Part part;
//
//    @ManyToOne
//    @MapsId("orderId")
//    @JoinColumn(name = "order_id")
//    Orders orders;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orderDetail_sequence")
    private Long orderDetailId;
    private Long orderId;
    private Long partId;
    private int quantity;
    private double price;

}
