package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeDto {
    private Long userId;
    private String email;
    private String position;
    private String contact;
    private String name;
    private String password;
    private String description;
}
