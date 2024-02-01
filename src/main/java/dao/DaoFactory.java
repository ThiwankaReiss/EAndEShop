package dao;

import dao.custom.impl.*;
import dao.util.DaoType;

public class DaoFactory {
    private  static  DaoFactory daoFactory;
    private DaoFactory(){

    }
    public static DaoFactory getInstance(){
        return daoFactory!=null? daoFactory:(daoFactory=new DaoFactory());
    }

    public <T extends SuperDao>T getDao(DaoType type){
        switch (type){
            case EMPLOYEE:return (T)new EmployeeDaoImpl();
            case USERHISTORY:return (T)new UserHistoryDaoImpl();
            case CUSTOMER:return (T)new CustomerDaoImpl();
            case ITEM:return (T)new ItemDaoImpl();
            case PART:return (T)new PartDaoImpl();
            case ORDER:return (T)new OrderDaoImpl();
            case ORDERDETAIL:return (T)new OrderDetailDaoImpl();
        }
        return null;
    }
}
