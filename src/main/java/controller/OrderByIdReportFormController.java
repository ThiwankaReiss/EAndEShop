package controller;

import bo.custom.EmployeeBo;
import bo.custom.OrderBo;
import bo.custom.OrderDetailBo;
import bo.custom.PartBo;
import bo.custom.impl.EmployeeBoImpl;
import bo.custom.impl.OrderBoImpl;
import bo.custom.impl.OrderDetailBoImpl;
import bo.custom.impl.PartBoImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.*;
import dto.tm.OrderDetailReoprtTm;
import dto.tm.OrderDetailsTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class OrderByIdReportFormController {
    public GridPane pane;
    public MenuItem salesReportBtn;
    public JFXTreeTableView<OrderDetailReoprtTm> partTbl;
    public TreeTableColumn colId;
    public TreeTableColumn colName;
    public TreeTableColumn colQuantity;
    public TreeTableColumn colPrice;
 

    public MenuButton orderIdMenuBtn;

    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;
    private EmployeeDto employeeDto;
    private Boolean positionStatus;
    private PartBo partBo=new PartBoImpl();
    
    ObservableList<OrderDetailReoprtTm> tmList = FXCollections.observableArrayList();
    private OrderBo orderBo=new OrderBoImpl();
    private OrderDto orderDto;

    public void initialize() throws SQLException, ClassNotFoundException {
        Long userId = UserInstanceController.getInstance().getUserId();
        allEmployees = employeeBo.getAll();
        for (EmployeeDto dto: allEmployees) {
            if(dto.getUserId().equals(userId)){
                employeeDto=dto;
            }
        }
        loadOrderIds();


        positionStatus=employeeDto.getPosition().equalsIgnoreCase("Admin");
        if(!positionStatus){
            salesReportBtn.setVisible(false);
        }
        colId.setCellValueFactory(new TreeItemPropertyValueFactory<>("partId"));
        colName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));




    }

    private void loadOrderIds() throws SQLException, ClassNotFoundException {
        List<OrderDto> dtoList = orderBo.getAll();

        for (OrderDto dto:dtoList) {
            MenuItem odr=new MenuItem(dto.getOrderId()+"");
            orderIdMenuBtn.getItems().add(odr);
            odr.setOnAction(e->{
                tmList.clear();
                orderDto = dto;
                loadTable();
                setData();
            });
        }
    }

    private void setData() {

    }


    private void loadTable() {


        try {

            List<OrderDetailDto> dtoList = orderDto.getList();

            for (OrderDetailDto dto:dtoList) {

                List<PartDto> parts=partBo.getAll();
                PartDto selectedDto = new PartDto();
                for (PartDto part:parts) {
                    if(part.getPartId().equals(dto.getPartId())){
                        selectedDto=part;
                    }
                }

                OrderDetailReoprtTm c = new OrderDetailReoprtTm(
                        dto.getPartId(),
                        selectedDto.getName(),
                        dto.getQuantity(),
                        dto.getPrice()
                );



                tmList.add(c);
            }
            try {
                TreeItem<OrderDetailReoprtTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);

                partTbl.setRoot(treeItem);
                partTbl.setShowRoot(false);
            }catch (Exception e){

            }

            TreeTableColumn [] t={colId,colPrice,colQuantity,colName};
            for (TreeTableColumn col:t) {
                col.setStyle("-fx-text-fill: white;-fx-background-color :  rgba(74, 101, 114, 1);");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
            stage.setTitle("Place Orders Form");
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
            stage.setTitle("Orders Form");
            stage.show();
        } catch (Exception e) {

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



    public void printOnAction(ActionEvent actionEvent) {

    }
}
