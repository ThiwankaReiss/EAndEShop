package controller;

import bo.custom.CustomerBo;
import bo.custom.EmployeeBo;
import bo.custom.OrderBo;
import bo.custom.impl.CustomerBoImpl;
import bo.custom.impl.EmployeeBoImpl;
import bo.custom.impl.OrderBoImpl;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import db.DBConnection;
import dto.CustomerDto;
import dto.EmployeeDto;
import dto.OrderDto;
import dto.tm.OrderByCustomerReportTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrdersByCustomerReportFormController {
    public GridPane pane;
    public MenuItem salesReportBtn;

    public MenuButton selectCustMenuBtn;
    public JFXTreeTableView<OrderByCustomerReportTm> ordersTbl;
    public TreeTableColumn colOrderId;
    public TreeTableColumn colEmployeeId;
    public TreeTableColumn colStatus;
    public TreeTableColumn colTotal;
    public TreeTableColumn colItemId;
    public TreeTableColumn colFee;
    public TreeTableColumn colDate;
    public Label custIdLbl;


    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;
    private EmployeeDto employeeDto;
    private Boolean positionStatus;
    private CustomerBo customerBo= new CustomerBoImpl();
    private CustomerDto customerDto;
    ObservableList<OrderByCustomerReportTm> tmList = FXCollections.observableArrayList();
    private OrderBo orderBo=new OrderBoImpl();

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
        colEmployeeId.setCellValueFactory(new TreeItemPropertyValueFactory<>("employeeId"));
        colItemId.setCellValueFactory(new TreeItemPropertyValueFactory<>("itemId"));
        colStatus.setCellValueFactory(new TreeItemPropertyValueFactory<>("status"));
        colTotal.setCellValueFactory(new TreeItemPropertyValueFactory<>("total"));
        colFee.setCellValueFactory(new TreeItemPropertyValueFactory<>("fee"));
        colDate.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        loadCustomers();
    }
    private void loadCustomers() throws SQLException, ClassNotFoundException {
        List<CustomerDto> dtoList = customerBo.getAll();

        for (CustomerDto dto:dtoList) {
            MenuItem odr=new MenuItem(dto.getContact()+"");
            selectCustMenuBtn.getItems().add(odr);
            odr.setOnAction(e->{
                tmList.clear();
                customerDto = dto;
                custIdLbl.setText("Customer Id : "+customerDto.getCustomerId());
                loadTable();
            });
        }
        for (CustomerDto dto:dtoList) {
            MenuItem odr=new MenuItem(dto.getCustomerId()+"");
            selectCustMenuBtn.getItems().add(odr);
            odr.setOnAction(e->{
                tmList.clear();
                customerDto = dto;
                custIdLbl.setText("Customer Id : "+customerDto.getCustomerId());
                loadTable();
            });
        }
    }

    private void loadTable() {


        try {
            List<OrderDto> orderDtos=orderBo.getAll();
            List<OrderDto> tmDtos=new ArrayList<>();
            for (OrderDto dto:orderDtos) {
                if(dto.getCustomerId().equals(customerDto.getCustomerId())){
                    tmDtos.add(dto);
                }
            }


            for (OrderDto dto:tmDtos) {



                OrderByCustomerReportTm c = new OrderByCustomerReportTm(
                        dto.getOrderId(),
                        dto.getEmployeeId(),
                        dto.getItemId(),
                        dto.getOrderStaus(),
                        dto.getTotal(),
                        dto.getFee(),
                        dto.getDate()

                );



                tmList.add(c);
            }
            try {
                TreeItem<OrderByCustomerReportTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);

                ordersTbl.setRoot(treeItem);
                ordersTbl.setShowRoot(false);
            }catch (Exception e){

            }

            TreeTableColumn [] t={colEmployeeId,colDate,colItemId,colStatus,colFee,colStatus,colOrderId,colTotal};
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

    public void printOnAction(ActionEvent actionEvent) throws JRException {
        if(customerDto !=null){

            JasperDesign design= JRXmlLoader.load("src/main/resources/reports/OrderByCustomerE&EShop.jrxml");

            JRDesignQuery query=new JRDesignQuery();
            query.setText("SELECT * FROM orders WHERE customerId='"+customerDto.getCustomerId()+"'");
            design.setQuery(query);

            Map<String, Object> parameters=new HashMap<String, Object>();

            parameters.put("CustomeIdText",customerDto.getCustomerId()+"");

            JasperReport jasperReport= JasperCompileManager.compileReport(design);
            JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport,parameters, DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jasperPrint,false);
        }else{
            new Alert(Alert.AlertType.ERROR,"Select A Customer").show();
        }
    }

}
