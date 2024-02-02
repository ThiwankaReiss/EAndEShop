package dto.tm;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class CustomerReportTm extends RecursiveTreeObject<CustomerReportTm> {
    private Long customerId;
    private String name;
    private String contact;
    private String email;
}
