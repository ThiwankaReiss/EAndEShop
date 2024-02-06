package dao.custom.impl;

import dao.custom.PartDao;
import dao.util.HibernateUtil;
import db.DBConnection;
import entity.Part;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PartDaoImpl implements PartDao {

    @Override
    public Long getNextId() throws SQLException {
        String sql = "SELECT * FROM part_sequence";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            return resultSet.getLong(1);
        }
        return null;
    }

    @Override
    public boolean save(Part entity) throws SQLException, ClassNotFoundException {
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
    public boolean update(Part entity) throws SQLException, ClassNotFoundException {
        Session session=HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        try {

            Part existingEntity = session.get(Part.class, entity.getPartId());


            existingEntity.setName(entity.getName());
            existingEntity.setCategory(entity.getCategory());
            existingEntity.setStandardFee(entity.getStandardFee());


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
        session.delete(session.find(Part.class,value));
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public List<Part> getAll() throws SQLException, ClassNotFoundException {
        Session session= HibernateUtil.getSession();
        Query query=session.createQuery("From Part ");
        List<Part> list = query.list();
        session.close();
        return list;
    }
}
