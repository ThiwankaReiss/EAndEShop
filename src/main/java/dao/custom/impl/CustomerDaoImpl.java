package dao.custom.impl;

import dao.custom.CustomerDao;
import dao.util.HibernateUtil;
import db.DBConnection;
import entity.Customer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {

    @Override
    public boolean save(Customer entity) throws SQLException, ClassNotFoundException {
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
    public boolean update(Customer entity) throws SQLException, ClassNotFoundException {
        Session session=HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        try {

            Customer existingEntity = session.get(Customer.class, entity.getCustomerId());


            existingEntity.setContact(entity.getContact());
            existingEntity.setName(entity.getName());
            existingEntity.setEmail(entity.getEmail());


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
        session.delete(session.find(Customer.class,value));
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public List<Customer> getAll() throws SQLException, ClassNotFoundException {
        Session session= HibernateUtil.getSession();
        Query query=session.createQuery("From Customer ");
        List<Customer> list = query.list();
        session.close();
        return list;
    }

    @Override
    public Long getNextId() throws SQLException {
        String sql = "SELECT * FROM customer_sequence";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            return resultSet.getLong(1);
        }
        return null;
    }

}
