package dto.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter

public class PartTm extends RecursiveTreeObject<PartTm> {
    private Long partId;
    private String name;
    private String category;
    private String standardFee;
    private JFXButton deleteBtn;
}
