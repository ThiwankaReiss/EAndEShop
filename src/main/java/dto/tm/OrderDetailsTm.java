package dto.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class OrderDetailsTm extends RecursiveTreeObject<OrderDetailsTm> {
    private Long partId;
    private String name;
    private int qty;
    private double price;
    private JFXButton btn;
}
