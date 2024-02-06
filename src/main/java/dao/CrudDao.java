package dao;

import java.sql.SQLException;
import java.util.List;

public interface CrudDao<T> extends SuperDao {
    boolean save (T entity) throws SQLException, ClassNotFoundException;
    boolean update(T entity) throws SQLException, ClassNotFoundException;
    boolean delete(Long value) throws SQLException, ClassNotFoundException;
    List<T> getAll() throws SQLException, ClassNotFoundException;
    Long getNextId() throws SQLException;
}