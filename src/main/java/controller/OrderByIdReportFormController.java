package controller;

import bo.custom.EmployeeBo;
import bo.custom.OrderBo;
import bo.custom.PartBo;
import bo.custom.impl.EmployeeBoImpl;
import bo.custom.impl.OrderBoImpl;
import bo.custom.impl.PartBoImpl;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import db.DBConnection;
import dto.EmployeeDto;
import dto.OrderDetailDto;
import dto.OrderDto;
import dto.PartDto;
import dto.tm.OrderDetailReoprtTm;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderByIdReportFormController {
    public GridPane pane;
    public MenuItem salesReportBtn;
    public JFXTreeTableView<OrderDetailReoprtTm> partTbl;
    public TreeTableColumn colId;
    public TreeTableColumn colName;
    public TreeTableColumn colQuantity;
    public TreeTableColumn colPrice;
 

    public MenuButton orderIdMenuBtn;
    public Label customerIdLbl;
    public Label orderIdLbl;
    public Label employeeIddLbl;
    public Label itemIdLbl;
    public Label lblTotal;
    public Label orderStatusLbl;
    public Label orderDate;
    public Label feeLbl;

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
        lblTotal.setText("Total : "+orderDto.getTotal());
        feeLbl.setText("Fee : "+orderDto.getFee());
        orderStatusLbl.setText("Status : "+orderDto.getOrderStaus());
        orderDate.setText("Date : "+orderDto.getDate());
        customerIdLbl.setText("Customer Id : "+orderDto.getCustomerId());
        orderIdLbl.setText("Order Id : "+orderDto.getOrderId());
        employeeIddLbl.setText("Employee Id : "+orderDto.getEmployeeId());
        itemIdLbl.setText("Item Id : "+orderDto.getItemId());
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



    public void printOnAction(ActionEvent actionEvent) throws JRException, SQLException, ClassNotFoundException {
        if(orderDto !=null){
            String s="";
            List<PartDto> partDtos=partBo.getAll();
            List<OrderDetailDto> dto=orderDto.getList();
            for (OrderDetailDto d:dto ) {
                for (PartDto p:partDtos) {
                    if(p.getPartId().equals(d.getPartId())){
                        s+=p.getPartId()+" - "+p.getName()+"\n";
                    }
                }
            }

            JasperDesign design= JRXmlLoader.load("src/main/resources/reports/OderByIdE&EShop.jrxml");

            JRDesignQuery query=new JRDesignQuery();
            query.setText("SELECT * FROM orderDetail WHERE orderId='"+orderDto.getOrderId()+"'");
            design.setQuery(query);

            Map<String, Object> parameters=new HashMap<String, Object>();
            parameters.put("OrderIdText",orderDto.getOrderId()+"");
            parameters.put("CustomerIdText",orderDto.getCustomerId()+"");
            parameters.put("ItemIdText",orderDto.getItemId()+"");
            parameters.put("EmployeeIdText",orderDto.getEmployeeId()+"");
            parameters.put("TotalText",orderDto.getTotal()+"");
            parameters.put("OrderStatusText",orderDto.getOrderStaus()+"");
            parameters.put("FeeText",orderDto.getFee()+"");
            parameters.put("DateText",orderDto.getDate()+"");

            parameters.put("PartNames",s);




            JasperReport jasperReport= JasperCompileManager.compileReport(design);
            JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport,parameters, DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jasperPrint,false);
        }else{
            new Alert(Alert.AlertType.ERROR,"Select An Order").show();
        }

    }
}
