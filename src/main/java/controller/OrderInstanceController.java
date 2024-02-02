package controller;

import dto.OrderDto;

import java.sql.SQLException;

public class OrderInstanceController {
    private static OrderInstanceController orderInstanceController;
    private static OrderDto orderDto;
    private OrderInstanceController() throws ClassNotFoundException, SQLException {

    }

    public static OrderInstanceController getInstance() throws ClassNotFoundException, SQLException {
        return orderInstanceController!=null ? orderInstanceController : (orderInstanceController=new OrderInstanceController());
    }

    public OrderDto getSelectedOrder(){
        return orderDto;
    }
    public void setSelectedOrder(OrderDto orderDto){
        this.orderDto=orderDto;
    }
}
