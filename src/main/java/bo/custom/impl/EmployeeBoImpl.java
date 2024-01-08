package bo.custom.impl;

import bo.custom.EmployeeBo;
import dao.DaoFactory;
import dao.custom.EmployeeDao;
import dao.util.DaoType;
import dto.EmployeeDto;
import entity.Employee;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeBoImpl implements EmployeeBo {
    private EmployeeDao employeeDao= DaoFactory.getInstance().getDao(DaoType.EMPLOYEE);

    @Override
    public List<EmployeeDto> allEmployees() throws SQLException, ClassNotFoundException {
        List<EmployeeDto> list=new ArrayList<>();
        List<Employee> entityLIst=employeeDao.getAll();

        for (Employee employee:entityLIst) {
            list.add(new EmployeeDto(
                    employee.getUserId(),
                    employee.getEmail(),
                    employee.getPosition(),
                    employee.getContact(),
                    employee.getName(),
                    employee.getPassword()
            ));
        }
        return list;
    }

    @Override
    public boolean saveEmployee(EmployeeDto dto) throws SQLException, ClassNotFoundException {
        return employeeDao.save(new Employee(
                dto.getUserId(),
                dto.getEmail(),
                dto.getPosition(),
                dto.getContact(),
                dto.getName(),
                dto.getPassword()
        ));
    }

}
