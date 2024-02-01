package dto.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class OrdersTm extends RecursiveTreeObject<OrdersTm> {
    private Long orderId;
    private Long customerId;
    private Long employeeId;
    private String status;
    private double total;
    private JFXButton changeStatusBtn;
    private JFXButton viewBtn;
    private JFXButton deleteBtn;
}
