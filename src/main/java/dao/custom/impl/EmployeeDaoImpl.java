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
    public boolean save(Employee entity) throws SQLException, ClassNotFoundException{

        try{
            Session session= HibernateUtil.getSession();
            Transaction transaction=session.beginTransaction();
            session.save(entity);
            transaction.commit();
            session.close();
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public boolean update(Employee entity) throws SQLException, ClassNotFoundException {
        Session session=HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        try {

            Employee existingEntity = session.get(Employee.class, entity.getUserId());

            existingEntity.setPassword(entity.getPassword());
            existingEntity.setContact(entity.getContact());
            existingEntity.setName(entity.getName());
            existingEntity.setEmail(entity.getEmail());
            existingEntity.setDescription(entity.getDescription());
            existingEntity.setPosition(entity.getPosition());

            session.update(existingEntity);

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return false;
    }

    @Override
    public boolean delete(Long value) throws SQLException, ClassNotFoundException {
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();
        session.delete(session.find(Employee.class,value));
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public List<Employee> getAll() throws SQLException, ClassNotFoundException {

        Session session= HibernateUtil.getSession();
        Query query=session.createQuery("From Employee ");
        List<Employee> list = query.list();
        session.close();
        return list;
    }

    @Override
    public Long getNextId() throws SQLException {
        return null;
    }
}
