package dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class OrderDto {
    private Long orderId;
    private Long customerId;
    private Long employeeId;
    private Long itemId;
    private String orderStaus;
    private double fee;
    private String date;
    private double total;
    private List<OrderDetailDto> list;
}
