package dao.custom.impl;

import dao.custom.OrderDetailDao;
import dao.util.HibernateUtil;
import dto.OrderDetailDto;
import entity.OrderDetail;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDaoImpl implements OrderDetailDao {

    @Override
    public boolean save(OrderDetailDto entity) throws SQLException, ClassNotFoundException {
        try{
            Session session= HibernateUtil.getSession();
            Transaction transaction=session.beginTransaction();

            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setOrderId(entity.getOrderId());
            orderDetail.setPartId(entity.getPartId());
            orderDetail.setQuantity(entity.getQuantity());
            orderDetail.setPrice(entity.getPrice());
            session.save(orderDetail);
            transaction.commit();
            session.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean update(OrderDetailDto entity) throws SQLException, ClassNotFoundException {
        Session session=HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        try {

            OrderDetail existingEntity = session.get(OrderDetail.class, entity.getOrderDetailId());

            existingEntity.setOrderId(entity.getOrderId());
            existingEntity.setPartId(entity.getPartId());
            existingEntity.setQuantity(entity.getQuantity());
            existingEntity.setPrice(entity.getPrice());

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
        session.delete(session.find(OrderDetail.class,value));
        transaction.commit();
        session.close();

        return true;
    }

    @Override
    public List<OrderDetailDto> getAll() throws SQLException, ClassNotFoundException {
        Session session= HibernateUtil.getSession();
        Query query=session.createQuery("From OrderDetail ");
        List<OrderDetail> list = query.list();
        session.close();
        List<OrderDetailDto> odrDetailDto=new ArrayList<>();
        for (OrderDetail odr:list) {
            odrDetailDto.add(new OrderDetailDto(
                    odr.getOrderDetailId(),
                    odr.getOrderId(),
                    odr.getPartId(),
                    odr.getQuantity(),
                    odr.getPrice()
            ));
        }


        return odrDetailDto;
    }


    @Override
    public Long getNextId() throws SQLException {
        return null;
    }
}
