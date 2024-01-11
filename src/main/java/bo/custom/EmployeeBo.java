package bo.custom;

import dto.EmployeeDto;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeBo {
    List<EmployeeDto> allEmployees() throws SQLException, ClassNotFoundException;

    boolean saveEmployee(EmployeeDto dto) throws SQLException, ClassNotFoundException;
    boolean updateEmployee(EmployeeDto dto) throws SQLException, ClassNotFoundException;
    boolean deleteEmployee(Long id) throws SQLException, ClassNotFoundException;
}
