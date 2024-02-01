package dao.custom.impl;

import dao.custom.UserHistoryDao;
import dao.util.HibernateUtil;
import entity.UserHistory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.util.List;

public class UserHistoryDaoImpl implements UserHistoryDao {
    @Override
    public boolean save(UserHistory entity) throws SQLException, ClassNotFoundException {
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();
        session.save(entity);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean update(UserHistory entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(Long value) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<UserHistory> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public Long getNextId() throws SQLException {
        return null;
    }
}
