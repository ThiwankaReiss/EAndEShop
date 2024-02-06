package dto.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter

public class EmployeeTm extends RecursiveTreeObject<EmployeeTm> {
    private Long userId;
    private String name;
    private String contact;
    private String email;
    private String position;
    private String password;
    private JFXButton deleteBtn;
}
