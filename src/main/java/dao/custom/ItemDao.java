package dao.custom;

import dao.CrudDao;
import entity.Employee;
import entity.Item;

import java.sql.SQLException;

public interface ItemDao extends CrudDao<Item> {
    Long getNextItemId() throws SQLException;
}
