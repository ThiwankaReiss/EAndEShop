package dto.tm;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class OrderByCustomerReportTm extends RecursiveTreeObject<OrderByCustomerReportTm> {
    private Long orderId;
    private Long employeeId;
    private Long itemId;
    private String status;
    private double total;
    private double fee;
    private String date;

}
