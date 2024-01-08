package bo;

import bo.custom.impl.EmployeeBoImpl;
import dao.util.BoType;

import java.sql.SQLException;

public class BoFactory {
    private  static BoFactory boFactory;
    private BoFactory(){

    }
    public static  BoFactory getInstance(){
        return boFactory!=null? boFactory:(boFactory=new BoFactory());
    }
    public <T extends SupperBo>T getBo(BoType type) throws SQLException, ClassNotFoundException {
        switch (type){
            case EMPLOYEE:return (T) new EmployeeBoImpl();
//            case ITEM:return (T) new CustomerBoImpl();
        }
        return null;
    }
}
