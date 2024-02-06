package dao.custom.impl;

import dao.DaoFactory;
import dao.custom.OrderDao;
import dao.custom.OrderDetailDao;
import dao.util.DaoType;
import dao.util.HibernateUtil;
import db.DBConnection;
import dto.OrderDetailDto;
import dto.OrderDto;
import entity.Orders;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {
    private OrderDetailDao orderDetailDao= DaoFactory.getInstance().getDao(DaoType.ORDERDETAIL);
    @Override
    public boolean save(OrderDto entity) throws SQLException, ClassNotFoundException {
        Session session= HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();
        Orders order= new Orders(
                entity.getOrderId(),
                entity.getCustomerId(),
                entity.getEmployeeId(),
                entity.getItemId(),
                entity.getOrderStaus(),
                entity.getFee(),
                entity.getDate(),
                entity.getTotal()
        );
        session.save(order);
        transaction.commit();
        session.close();


        List<OrderDetailDto> list=entity.getList();
        if(list!=null){
            for (OrderDetailDto detailsDto:list) {

                orderDetailDao.save(detailsDto);

            }
        }



        return true;
    }

    @Override
    public boolean update(OrderDto entity) throws SQLException, ClassNotFoundException {
        Session session=HibernateUtil.getSession();
        Transaction transaction=session.beginTransaction();

        try {

            Orders existingEntity = session.get(Orders.class, entity.getOrderId());


            existingEntity.setOrderStaus(entity.getOrderStaus());
            existingEntity.setTotal(entity.getTotal());


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
        session.delete(session.find(Orders.class,value));
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public List<OrderDto> getAll() throws SQLException, ClassNotFoundException {
        Session session= HibernateUtil.getSession();
        Query query=session.createQuery("From Orders ");
        List<Orders> list = query.list();
        session.close();
        List<OrderDto> odrDto=new ArrayList<>();
        List<OrderDetailDto> orderDetailDtoList=orderDetailDao.getAll();
        for (Orders odr:list) {
            List orderDetailArray=new ArrayList<>();
            for (OrderDetailDto odrDetail:orderDetailDtoList) {
                if(odrDetail.getOrderId().equals(odr.getOrderId())){
                    orderDetailArray.add(odrDetail);
                }
            }
            odrDto.add(new OrderDto(
                    odr.getOrderId(),
                    odr.getCustomerId(),
                    odr.getEmployeeId(),
                    odr.getItemId(),
                    odr.getOrderStaus(),
                    odr.getFee(),
                    odr.getDate(),
                    odr.getTotal(),
                    orderDetailArray
            ));
        }


        return odrDto;

    }

    @Override
    public Long getNextId() throws SQLException {
        String sql = "SELECT * FROM order_sequence";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            return resultSet.getLong(1);
        }
        return null;
    }
}
