package dto.tm;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class OrderDetailReoprtTm extends RecursiveTreeObject<OrderDetailReoprtTm> {
    private Long partId;
    private String name;
    private int qty;
    private double price;
}
