package dao.custom.impl;

import dao.custom.EmployeeDao;
import dao.util.HibernateUtil;
import entity.Employee;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;

public class EmployeeDaoImpl implements EmployeeDao {
    @Override
    public boolean save(Employee entity) throws SQLException, ClassNotFoundException {
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();
        session.save(entity);
        transaction.commit();
        session.close();
        return true;


    }

    @Override
    public boolean update(Employee entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String value) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<Employee> getAll() throws SQLException, ClassNotFoundException {

        Session session= HibernateUtil.getSession();
        Query query=session.createQuery("From Employee ");
        List<Employee> list = query.list();
        session.close();
        return list;
    }
}
