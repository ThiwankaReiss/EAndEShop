package dao.custom;

import dao.CrudDao;
import entity.Customer;
import entity.Employee;

import java.sql.SQLException;

public interface CustomerDao extends CrudDao<Customer> {
    public Long getNextCustId() throws SQLException;
}
