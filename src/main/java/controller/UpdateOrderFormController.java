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
import dto.EmployeeDto;
import dto.OrderDetailDto;
import dto.OrderDto;
import dto.PartDto;
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

public class UpdateOrderFormController {
    public GridPane pane;
    public MenuItem salesReportBtn;
    public JFXTreeTableView<OrderDetailsTm> partTbl;
    public TreeTableColumn colId;
    public TreeTableColumn colName;
    public TreeTableColumn colQuantity;
    public TreeTableColumn colPrice;
    public TreeTableColumn colDelete;
    public JFXTextField partQtyTextField;
    public JFXTextField unitPriceTextField;
    public JFXTextField priceTextField;
    public JFXButton newPartBtn;
    public JFXTextField addPartTextField;
    public MenuButton partMenuBtn;
    public Label orderIdLbl;
    public Label customerIdLbl;
    public Label employeeIddLbl;
    public Label orderDate;
    public Label lblTotal;
    public JFXTextField orderStatusTextField;
    public MenuButton statusMenuBtn;
    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;
    private EmployeeDto employeeDto;
    private Boolean positionStatus;
    private OrderDetailsTm selectedUpTm;
    private PartBo partBo=new PartBoImpl();
    private PartDto selectedPart;
    private OrderDetailBo orderDetailBo=new OrderDetailBoImpl();
    private OrderDto orderDto ;
    private double inifee=0.0;
    private double tot = 0;
    ObservableList<OrderDetailsTm> tmList = FXCollections.observableArrayList();
    private OrderBo orderBo=new OrderBoImpl();


    public void initialize() throws SQLException, ClassNotFoundException {
        Long userId = UserInstanceController.getInstance().getUserId();
        allEmployees = employeeBo.getAll();
        for (EmployeeDto dto: allEmployees) {
            if(dto.getUserId().equals(userId)){
                employeeDto=dto;
            }
        }

        orderDto = OrderInstanceController.getInstance().getSelectedOrder();
        lblTotal.setText("Total : "+orderDto.getTotal());
        orderIdLbl.setText("Order Id : "+orderDto.getOrderId());
        customerIdLbl.setText("Customer Id : "+orderDto.getCustomerId());
        employeeIddLbl.setText("Placed employee Id : "+orderDto.getEmployeeId());
        orderDate.setText("Placed date : "+orderDto.getDate());
        positionStatus=employeeDto.getPosition().equalsIgnoreCase("Admin");
        if(!positionStatus){
            salesReportBtn.setVisible(false);
        }
        colId.setCellValueFactory(new TreeItemPropertyValueFactory<>("partId"));
        colName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colDelete.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        loadTable();
        loadParts();
        loadStatus();
        partTbl.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                setData(newValue.getValue());
            }catch (NullPointerException e){

            }
        });


    }
    private void setData(OrderDetailsTm newValue) {

        addPartTextField.setText(newValue.getPartId()+"-"+newValue.getName());
        partQtyTextField.setText(String.valueOf(newValue.getQty()));
        unitPriceTextField.setText(String.valueOf(newValue.getPrice()/newValue.getQty()));
        priceTextField.setText(String.valueOf(newValue.getPrice()));
        selectedUpTm=newValue;
    }

    private void loadTable() {


        try {

            List<OrderDetailDto> dtoList = orderDto.getList();

            for (OrderDetailDto dto:dtoList) {
                JFXButton btn = new JFXButton("Delete");
                List<PartDto> parts=partBo.getAll();
                PartDto selectedDto = new PartDto();
                for (PartDto part:parts) {
                    if(part.getPartId().equals(dto.getPartId())){
                        selectedDto=part;
                    }
                }

                OrderDetailsTm c = new OrderDetailsTm(
                        dto.getPartId(),
                        selectedDto.getName(),
                        dto.getQuantity(),
                        dto.getPrice(),
                        btn
                );

                btn.setStyle("-fx-background-color: white");
                btn.setOnAction(actionEvent -> {
                    try {
                        boolean isDelete=orderDetailBo.delete(dto.getOrderDetailId());
                        if(isDelete){
                            new Alert(Alert.AlertType.INFORMATION,"Part was removed from the order").show();
                        }else{
                            new Alert(Alert.AlertType.ERROR,"Something went wrong").show();
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
                TreeItem<OrderDetailsTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);

                partTbl.setRoot(treeItem);
                partTbl.setShowRoot(false);
            }catch (Exception e){

            }

            TreeTableColumn [] t={colId,colDelete,colPrice,colQuantity,colName};
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
    public static boolean containsOnlyNumbers(String input,boolean inte) {
        // Define a regular expression pattern for numbers with decimals
        String numericPattern;
        if(inte){
            numericPattern = "^[0-9]+$";
        }else{
            numericPattern = "^[0-9]+(\\.[0-9]+)?$";
        }



        // Compile the pattern
        Pattern pattern = Pattern.compile(numericPattern);

        // Check if the input matches the pattern
        return pattern.matcher(input).matches();
    }
    private void loadParts() throws SQLException, ClassNotFoundException {
        List<PartDto> dtoList = partBo.getAll();

        for (PartDto dto:dtoList) {
            MenuItem itm=new MenuItem(dto.getPartId()+"-"+dto.getName());
            partMenuBtn.getItems().add(itm);
            itm.setOnAction(e->{
                addPartTextField.setText(itm.getText());
                selectedPart=dto;
                unitPriceTextField.setText(selectedPart.getStandardFee());
                setPrice();
            });
        }
    }


    private void setPrice(){
        if(!((unitPriceTextField.getText()==null|| unitPriceTextField.getText().equals(""))
                && (partQtyTextField.getText()==null||!partQtyTextField.getText().equals(""))))
        {
            if( containsOnlyNumbers(partQtyTextField.getText(),true) &&Integer.parseInt(partQtyTextField.getText())>0
                    && containsOnlyNumbers(unitPriceTextField.getText(),false)
            ){
                priceTextField.setText(String.valueOf(Double.parseDouble(unitPriceTextField.getText())*Double.parseDouble(partQtyTextField.getText())));
            }
        }else if(!((selectedPart==null|| selectedPart.equals(""))
                && (partQtyTextField.getText()==null||!partQtyTextField.getText().equals("")))

        ){
            if( containsOnlyNumbers(partQtyTextField.getText(),true) &&Integer.parseInt(partQtyTextField.getText())>0){
                priceTextField.setText(String.valueOf(Double.parseDouble(selectedPart.getStandardFee())*Double.parseDouble(partQtyTextField.getText())));
            }

        }
    }
    public void setTotal() throws SQLException, ClassNotFoundException {
        orderDto = OrderInstanceController.getInstance().getSelectedOrder();
        tot+=orderDto.getTotal()-inifee;
        inifee=orderDto.getTotal();
        lblTotal.setText(String.valueOf(tot));
    }
    public void partQtyKeyReleased(KeyEvent keyEvent) {
        setPrice();
    }

    public void unitPriceOnKeyReleased(KeyEvent keyEvent) throws SQLException, ClassNotFoundException {
        setTotal();
    }

    public void newPartBtnOnAction(ActionEvent actionEvent) {
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

    public void updatePartOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(partQtyTextField.getText()!=null && unitPriceTextField.getText()!=null
                && containsOnlyNumbers(partQtyTextField.getText(),true)
                && containsOnlyNumbers(unitPriceTextField.getText(),false) ){

            tot -= selectedUpTm.getPrice();


            JFXButton btn = new JFXButton("Delete");
            List<PartDto> parts=partBo.getAll();
            PartDto selectedDto = new PartDto();
            System.out.println(selectedUpTm.getPartId());


            Long extractedNumber = selectedUpTm.getPartId();
            System.out.println(extractedNumber);
            for (PartDto dto:parts) {
                if(dto.getPartId()==extractedNumber){
                    selectedDto=dto;
                }
            }

            OrderDetailsTm tm = new OrderDetailsTm(
                    selectedDto.getPartId(),
                    selectedDto.getName(),
                    Integer.parseInt(partQtyTextField.getText()),
                    Double.parseDouble(priceTextField.getText())*Double.parseDouble(partQtyTextField.getText()),
                    btn
            );

            btn.setOnAction(actionEvent1 -> {
                tmList.remove(tm);
                tot -= tm.getPrice();
                partTbl.refresh();
                lblTotal.setText(String.format("%.2f",tot));
            });

            boolean isExist = false;

            for (OrderDetailsTm order:tmList) {
                if (order.getPartId().equals(tm.getPartId())){
                    order.setQty(order.getQty()+tm.getQty()-selectedUpTm.getQty());
                    order.setPrice(order.getPrice()+tm.getPrice()-selectedUpTm.getPrice());
                    isExist = true;
                    tot+=tm.getPrice();
                }
            }

            if (!isExist){
                tmList.add(tm);
                tot+= tm.getPrice()-selectedUpTm.getPrice();
            }

            TreeItem<OrderDetailsTm> treeObject = new RecursiveTreeItem<OrderDetailsTm>(tmList, RecursiveTreeObject::getChildren);
            partTbl.setRoot(treeObject);
            partTbl.setShowRoot(false);

            lblTotal.setText(String.format("%.2f",tot));
            OrderDetailDto selectDto=new OrderDetailDto();
            for (OrderDetailDto t: orderDto.getList()) {
                if(selectedUpTm.getPartId().equals(t.getPartId())){
                    selectDto=t;
                }
            }

            OrderDetailDto orderDetailDto=new OrderDetailDto();
            orderDetailDto.setOrderDetailId(selectDto.getOrderDetailId());
            orderDetailDto.setOrderId(orderDto.getOrderId());
            orderDetailDto.setPartId(tm.getPartId());
            orderDetailDto.setQuantity(tm.getQty());
            orderDetailDto.setPrice(tm.getPrice());
            boolean isDetailSaved=orderDetailBo.update(orderDetailDto);

            OrderDto dt=new OrderDto();
            dt.setOrderId(orderDto.getOrderId());
            dt.setTotal(tot);
            dt.setOrderStaus(orderDto.getOrderStaus());
            boolean isOrderSaved= orderBo.update(dt);
            if(isDetailSaved && isOrderSaved){
                new Alert(Alert.AlertType.INFORMATION,"Part updated successfully").show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong").show();
            }

            selectedUpTm=null;
            clearFields();


        }
    }
    private void clearFields() {
        partTbl.refresh();
        selectedPart=null;
        JFXTextField[] textFields = {addPartTextField, partQtyTextField
                , unitPriceTextField, priceTextField};
        for (JFXTextField textField:textFields) {
            textField.clear();
        }
    }
    public void addPartOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(partQtyTextField.getText()==null || partQtyTextField.getText().equals("")){
            new Alert(Alert.AlertType.ERROR,"Enter Number of parts").show();
        }else if(!containsOnlyNumbers(partQtyTextField.getText(),true)){
            new Alert(Alert.AlertType.ERROR,"Part must contain only integer numbers").show();
        }
        if(priceTextField.getText()==null || priceTextField.getText().equals("")){
            new Alert(Alert.AlertType.ERROR,"Enter price").show();
        }else if(!containsOnlyNumbers(partQtyTextField.getText(),false)){
            new Alert(Alert.AlertType.ERROR,"Price must contain only numbers").show();
        }

        if(addPartTextField.getText()==null|| addPartTextField.getText().equals("")){
            new Alert(Alert.AlertType.ERROR,"Select Part price").show();
        }
        if(partQtyTextField.getText()!=null && priceTextField.getText()!=null
                && containsOnlyNumbers(partQtyTextField.getText(),true)
                && containsOnlyNumbers(priceTextField.getText(),false)
                && Integer.parseInt(partQtyTextField.getText())>0
                && Double.parseDouble(priceTextField.getText())>0
                && selectedUpTm==null
                && addPartTextField.getText()!=null
                && !addPartTextField.getText().equals("")
        ){


            JFXButton btn = new JFXButton("Delete");

            OrderDetailsTm tm = new OrderDetailsTm(
                    selectedPart.getPartId(),
                    selectedPart.getName(),
                    Integer.parseInt(partQtyTextField.getText()),
                    Double.parseDouble(priceTextField.getText()),
                    btn
            );

            btn.setOnAction(actionEvent1 -> {
                tmList.remove(tm);
                tot -= tm.getPrice();
                partTbl.refresh();
                lblTotal.setText(String.format("%.2f",tot));
            });

            boolean isExist = false;

            for (OrderDetailsTm order:tmList) {
                if (order.getPartId().equals(tm.getPartId())){
                    order.setQty(order.getQty()+tm.getQty());
                    order.setPrice(order.getPrice()+tm.getPrice());
                    isExist = true;
                    tot+=tm.getPrice();
                }
            }

            if (!isExist){
                tmList.add(tm);
                tot+= tm.getPrice();
            }

            TreeItem<OrderDetailsTm> treeObject = new RecursiveTreeItem<OrderDetailsTm>(tmList, RecursiveTreeObject::getChildren);
            partTbl.setRoot(treeObject);
            partTbl.setShowRoot(false);

            lblTotal.setText(String.format("%.2f",tot));

            clearFields();
            OrderDetailDto orderDetailDto=new OrderDetailDto();
            orderDetailDto.setOrderId(orderDto.getOrderId());
            orderDetailDto.setPartId(tm.getPartId());
            orderDetailDto.setQuantity(tm.getQty());
            orderDetailDto.setPrice(tm.getPrice());
            boolean isDetailSaved=orderDetailBo.save(orderDetailDto);

            OrderDto dt=new OrderDto();
            dt.setOrderId(orderDto.getOrderId());
            dt.setTotal(tot);
            dt.setOrderStaus(orderDto.getOrderStaus());
            boolean isOrderSaved= orderBo.update(dt);
            if(isDetailSaved && isOrderSaved){
                new Alert(Alert.AlertType.INFORMATION,"Part added successfully").show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong").show();
            }
            selectedPart=null;

        }else if(selectedUpTm!=null){

            new Alert(Alert.AlertType.INFORMATION,"Click Update").show();

        }
    }

    public void reloadBtnOnAction(ActionEvent actionEvent) {
        clearFields();
    }
    private void loadStatus() {
        String [] status={"Pending","Processing","completed"};
        for (String stat:status) {
            MenuItem itm=new MenuItem(stat);
            statusMenuBtn.getItems().add(itm);
            itm.setOnAction(e->{
                orderStatusTextField.setText(itm.getText());
                orderDto.setOrderStaus(itm.getText());
                OrderDto dt=new OrderDto();
                dt.setOrderId(orderDto.getOrderId());
                dt.setTotal(tot);
                dt.setOrderStaus(orderDto.getOrderStaus());
                boolean isOrderSaved= false;
                try {
                    isOrderSaved = orderBo.update(dt);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                if(isOrderSaved){
                    new Alert(Alert.AlertType.INFORMATION,"Updated successfully").show();
                }else{
                    new Alert(Alert.AlertType.ERROR,"Something went wrong").show();
                }
            });
        }

    }


}
