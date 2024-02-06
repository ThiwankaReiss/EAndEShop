package controller;

import bo.custom.EmployeeBo;
import bo.custom.OrderBo;
import bo.custom.OrderDetailBo;
import bo.custom.impl.EmployeeBoImpl;
import bo.custom.impl.OrderBoImpl;
import bo.custom.impl.OrderDetailBoImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import db.DBConnection;
import dto.EmployeeDto;
import dto.OrderDetailDto;
import dto.OrderDto;
import dto.tm.OrdersTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class OrderFormController {
    public GridPane pane;
    public MenuItem salesReportBtn;
    public TreeTableColumn colCustId;
    public TreeTableColumn colStatus;
    public TreeTableColumn colOrderId;
    public TreeTableColumn colTotal;
    public TreeTableColumn colChangeStatus;
    public TreeTableColumn colView;
    public TreeTableColumn colDelete;
    public JFXTreeTableView<OrdersTm> ordersTbl;
    public TreeTableColumn colEmployeeId;
    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private OrderBo orderBo=new OrderBoImpl();
    private List<EmployeeDto> allEmployees;
    private EmployeeDto employeeDto;
    private Boolean positionStatus;
    private OrderDetailBo orderDetailBo=new OrderDetailBoImpl();
    public void initialize() throws SQLException, ClassNotFoundException {
        Long userId = UserInstanceController.getInstance().getUserId();
        allEmployees = employeeBo.getAll();
        for (EmployeeDto dto: allEmployees) {
            if(dto.getUserId().equals(userId)){
                employeeDto=dto;
            }
        }
        positionStatus=employeeDto.getPosition().equalsIgnoreCase("Admin");
        if(!positionStatus){
            salesReportBtn.setVisible(false);
        }


        colOrderId.setCellValueFactory(new TreeItemPropertyValueFactory<>("orderId"));
        colCustId.setCellValueFactory(new TreeItemPropertyValueFactory<>("customerId"));
        colEmployeeId.setCellValueFactory(new TreeItemPropertyValueFactory<>("employeeId"));
        colStatus.setCellValueFactory(new TreeItemPropertyValueFactory<>("status"));
        colTotal.setCellValueFactory(new TreeItemPropertyValueFactory<>("total"));
        colChangeStatus.setCellValueFactory(new TreeItemPropertyValueFactory<>("changeStatusBtn"));
        colView.setCellValueFactory(new TreeItemPropertyValueFactory<>("viewBtn"));
        colDelete.setCellValueFactory(new TreeItemPropertyValueFactory<>("deleteBtn"));

        loadTable();
//
//        ordersTbl.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
//            try {
//                setData(newValue.getValue());
//            }catch (NullPointerException e){
//
//            }
//        });
    }

    private void loadTable() {
        ObservableList<OrdersTm> tmList = FXCollections.observableArrayList();

        try {
            List<OrderDto> dtoList = orderBo.getAll();

            for (OrderDto dto:dtoList) {
                JFXButton deleteBtn = new JFXButton("Delete");
                JFXButton changeStatusBtn = new JFXButton("Change Status");
                JFXButton viewBtn = new JFXButton("View");
                OrdersTm c = new OrdersTm(
                        dto.getOrderId(),
                        dto.getCustomerId(),
                        dto.getEmployeeId(),
                        dto.getOrderStaus(),
                        dto.getTotal(),
                        changeStatusBtn,
                        viewBtn,
                        deleteBtn
                );

                deleteBtn.setStyle("-fx-background-color: white");
                deleteBtn.setOnAction(actionEvent -> {
                    deleteOrder(dto,c);

                });
                changeStatusBtn.setStyle("-fx-background-color: white");
                changeStatusBtn.setOnAction(actionEvent -> {
                    System.out.println("status");
                    try {
                        setOrderInstance(dto);
                        Stage stage = (Stage) pane.getScene().getWindow();
                        try {
                            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/UpdateOrderForm.fxml"))));
                            stage.setResizable(true);
                            stage.setTitle("Update Order From");
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
                viewBtn.setStyle("-fx-background-color: white");
                viewBtn.setOnAction(actionEvent -> {
                    System.out.println("add");
                    try {
                        setOrderInstance(dto);
                        Stage stage = (Stage) pane.getScene().getWindow();
                        try {
                            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/UpdateOrderForm.fxml"))));
                            stage.setResizable(true);
                            stage.setTitle("Update Order From");
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });



                tmList.add(c);
            }
            try {
                TreeItem<OrdersTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);

                ordersTbl.setRoot(treeItem);
                ordersTbl.setShowRoot(false);
            }catch (Exception e){

            }

            TreeTableColumn [] t={colCustId,colEmployeeId,colOrderId,colStatus,colTotal,colChangeStatus,colView,colDelete};
            for (TreeTableColumn col:t) {
                col.setStyle("-fx-text-fill: white;-fx-background-color :  rgba(74, 101, 114, 1);");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteOrder(OrderDto dto,OrdersTm c) {
        try {
            List<OrderDetailDto> list=dto.getList();
            for (OrderDetailDto detailDto:list) {
                orderDetailBo.delete(detailDto.getOrderDetailId());
            }
            boolean isDeleted=orderBo.delete(c.getOrderId());
            if(isDeleted){

                new Alert(Alert.AlertType.INFORMATION,"Is deleted").show();
                Stage stage = (Stage) pane.getScene().getWindow();
                try {
                    stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/OrderForm.fxml"))));
                    stage.setResizable(true);
                    stage.setTitle("Orders Form");
                    stage.show();
                } catch (Exception e) {

                }
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong").show();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setOrderInstance(OrderDto orderDto) throws SQLException, ClassNotFoundException {
        OrderInstanceController.getInstance().setSelectedOrder(orderDto);
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

    public void printOnAction(ActionEvent actionEvent) throws JRException {
        JasperDesign design= JRXmlLoader.load("src/main/resources/reports/AllOrderReportE&Eshop.jrxml");
        JasperReport jasperReport= JasperCompileManager.compileReport(design);
        JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport,null, DBConnection.getInstance().getConnection());
        JasperViewer.viewReport(jasperPrint,false);
    }
}
