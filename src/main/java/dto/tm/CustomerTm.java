package dto.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class CustomerTm extends RecursiveTreeObject<CustomerTm> {
    private Long customerId;
    private String name;
    private String contact;
    private String email;
    private JFXButton deleteBtn;
}
