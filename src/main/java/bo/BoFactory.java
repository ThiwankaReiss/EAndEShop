package bo;

import bo.custom.impl.CustomerBoImpl;
import bo.custom.impl.EmployeeBoImpl;
import bo.custom.impl.PartBoImpl;
import bo.util.BoType;

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
//            case EMPLOYEE:return (T) new EmployeeBoImpl();
//            case CUSTOMER:return (T) new CustomerBoImpl();
//            case ITEM:return (T) new CustomerBoImpl();
            case PART:return (T) new PartBoImpl();
        }
        return null;
    }
}
