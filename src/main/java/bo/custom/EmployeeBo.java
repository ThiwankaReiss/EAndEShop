package bo.custom;

import dto.EmployeeDto;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeBo {
    List<EmployeeDto> allEmployees() throws SQLException, ClassNotFoundException;

    void saveEmployee(EmployeeDto dto) throws SQLException, ClassNotFoundException;
    boolean updateEmployee(EmployeeDto dto) throws SQLException, ClassNotFoundException;
}
