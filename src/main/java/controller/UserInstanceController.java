package controller;

import java.sql.SQLException;

public class UserInstanceController {
    private static UserInstanceController userInstanceController;
    private static Long userId;
    private UserInstanceController() throws ClassNotFoundException, SQLException {

    }

    public static UserInstanceController getInstance() throws ClassNotFoundException, SQLException {
        return userInstanceController!=null ? userInstanceController : (userInstanceController=new UserInstanceController());
    }

    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId=userId;
    }
}
