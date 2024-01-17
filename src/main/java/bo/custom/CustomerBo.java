package bo.custom;

import dto.CustomerDto;
import dto.EmployeeDto;

import java.sql.SQLException;
import java.util.List;

public interface CustomerBo {
    List<CustomerDto> allCustomers() throws SQLException, ClassNotFoundException;

    boolean saveCustomer(CustomerDto dto) throws SQLException, ClassNotFoundException;
    boolean updateCustomer(CustomerDto dto) throws SQLException, ClassNotFoundException;
    boolean deleteCustomer(Long id) throws SQLException, ClassNotFoundException;
    public Long getNextCustId() throws SQLException;
}
