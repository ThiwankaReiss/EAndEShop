package dao;

import dao.custom.impl.EmployeeDaoImpl;
import dao.custom.impl.UserHistoryDaoImpl;
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
        }
        return null;
    }
}
