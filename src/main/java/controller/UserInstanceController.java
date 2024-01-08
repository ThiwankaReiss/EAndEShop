package controller;

import lombok.Setter;

import java.sql.SQLException;

public class UserInstanceController {
    private static UserInstanceController userInstanceController;
    private static String userId;
    private UserInstanceController() throws ClassNotFoundException, SQLException {

    }

    public static UserInstanceController getInstance() throws ClassNotFoundException, SQLException {
        return userInstanceController!=null ? userInstanceController : (userInstanceController=new UserInstanceController());
    }

    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
}
