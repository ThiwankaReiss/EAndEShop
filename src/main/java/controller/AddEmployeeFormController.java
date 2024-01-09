package controller;

import bo.custom.EmployeeBo;
import bo.custom.impl.EmployeeBoImpl;
import dto.EmployeeDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AddEmployeeFormController {
    public GridPane pane;
    public MenuItem salesReportBtn;
    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;

    private EmployeeDto employeeDto;

    private Boolean positionStatus;

    public void initialize() throws SQLException, ClassNotFoundException {
        String userId = UserInstanceController.getInstance().getUserId();
        allEmployees = employeeBo.allEmployees();
        for (EmployeeDto dto: allEmployees) {
            if(dto.getUserId().equals(userId)){
                employeeDto=dto;
            }
        }
        positionStatus=employeeDto.getPosition().equalsIgnoreCase("Admin");
        if(!positionStatus){
            salesReportBtn.setVisible(false);
        }

    }

    public void orderReportBtnOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/OrderReportForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Orders Report Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void customerReportBtnOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/CustomerReportForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Customers Report Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void salesReportBtnOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/SalesReportForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Sales Report Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void homeOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/EmployeeHomeForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Employee Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void customerOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/CustomerForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Customer Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void placeOrderOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/PlaceOrderForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Place Order Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ordersOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/OrderForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Order Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void itemsOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/ItemForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Item From");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void partsOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/PartForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Parts Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
