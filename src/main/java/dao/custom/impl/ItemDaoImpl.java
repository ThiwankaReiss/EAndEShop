package dao.custom.impl;

import dao.custom.ItemDao;
import dao.util.HibernateUtil;
import db.DBConnection;
import entity.Item;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemDaoImpl implements ItemDao {
    @Override
    public boolean save(Item entity) throws SQLException, ClassNotFoundException {
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
    public boolean update(Item entity) throws SQLException, ClassNotFoundException {
        Session session=HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        try {

            Item existingEntity = session.get(Item.class, entity.getItemId());


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
        session.delete(session.find(Item.class,value));
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public List<Item> getAll() throws SQLException, ClassNotFoundException {
        Session session= HibernateUtil.getSession();
        Query query=session.createQuery("From Item ");
        List<Item> list = query.list();
        session.close();
        return list;
    }

    @Override
    public Long getNextId() throws SQLException {
        String sql = "SELECT * FROM item_sequence";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            return resultSet.getLong(1);
        }
        return null;
    }

}
