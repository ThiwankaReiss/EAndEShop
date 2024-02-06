package bo.custom.impl;

import bo.custom.OrderBo;
import dao.DaoFactory;
import dao.custom.OrderDao;
import dao.util.DaoType;
import dto.OrderDto;

import java.sql.SQLException;
import java.util.List;

public class OrderBoImpl implements OrderBo {
    private OrderDao orderDao= DaoFactory.getInstance().getDao(DaoType.ORDER);
    @Override
    public boolean save(OrderDto dto) throws SQLException, ClassNotFoundException {
        return orderDao.save(dto);
    }

    @Override
    public boolean update(OrderDto dto) throws SQLException, ClassNotFoundException {
        return orderDao.update(dto);
    }

    @Override
    public boolean delete(Long value) throws SQLException, ClassNotFoundException {
        return orderDao.delete(value);
    }

    @Override
    public List<OrderDto> getAll() throws SQLException, ClassNotFoundException {
        return orderDao.getAll();
    }

    @Override
    public Long getNextId() throws SQLException {
        return orderDao.getNextId();
    }
}
