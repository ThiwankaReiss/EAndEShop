package dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeDto {
    private String userId;
    private String email;
    private String position;
    private String contact;
    private String name;
    private String password;
}
