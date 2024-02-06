package bo;

import java.sql.SQLException;
import java.util.List;

public interface CrudBo <T> extends SupperBo {
    boolean save (T dto) throws SQLException, ClassNotFoundException;
    boolean update(T dto) throws SQLException, ClassNotFoundException;
    boolean delete(Long value) throws SQLException, ClassNotFoundException;
    List<T> getAll() throws SQLException, ClassNotFoundException;
    Long getNextId() throws SQLException;
}