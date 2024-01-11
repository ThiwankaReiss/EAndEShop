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
                    employee.getPassword(),
                    employee.getDescription()
            ));
        }
        return list;
    }

    @Override
    public boolean saveEmployee(EmployeeDto dto) throws SQLException, ClassNotFoundException {
        Employee employee=new Employee();
        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setContact(dto.getContact());
        employee.setPosition(dto.getPosition());
        employee.setPassword(dto.getPassword());
        employee.setDescription(dto.getDescription());
        return employeeDao.save(employee);
    }

    public boolean updateEmployee(EmployeeDto dto) throws SQLException, ClassNotFoundException {
        return employeeDao.update(new Employee(
                dto.getUserId(),
                dto.getEmail(),
                dto.getPosition(),
                dto.getContact(),
                dto.getName(),
                dto.getPassword(),
                dto.getDescription()
        ));

    }

    public boolean deleteEmployee(Long id) throws SQLException, ClassNotFoundException {
        return employeeDao.delete(id);
    }

}
