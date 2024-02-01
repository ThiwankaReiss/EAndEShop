package dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class OrderDetailDto {
    private Long orderId;
    private Long partId;
    private int quantity;
    private double price;
}
