package controller;

import bo.custom.*;
import bo.custom.impl.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.*;
import dto.tm.OrderDetailsTm;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PlaceOrderFormController {
    public GridPane pane;
    public MenuItem salesReportBtn;
    public Label lblDate;
    public Label lblTime;
    public MenuButton itmMenuBtn;
    public MenuButton custMenuBtn;
    public MenuButton statusMenuBtn;
    public MenuButton partMenuBtn;
    public JFXTextField selectCustTextField;
    public JFXTextField selectItmTextField;
    public JFXTextField orderStatusTextField;
    public JFXTextField feeTextField;
    public JFXTextField addPartTextField;
    public JFXTextField partQtyTextField;
    public JFXTextField unitPriceTextField;
    public JFXTextField nameTextField;
    public JFXButton newPartBtn;
    public JFXTreeTableView<OrderDetailsTm> partTbl;
    public TreeTableColumn colId;
    public TreeTableColumn colName;
    public TreeTableColumn colQuantity;
    public TreeTableColumn colPrice;
    public TreeTableColumn colDelete;
    public JFXTextField priceTextField;
    public Label lblTotal;
    public Label employeeIdLbl;
    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;
    private EmployeeDto employeeDto;
    private Boolean positionStatus;
    private CustomerBo customerBo=new CustomerBoImpl();
    private ItemBo itemBo=new ItemBoImpl();
    private PartBo partBo=new PartBoImpl();
    private CustomerDto selectedCustomer;
    private ItemDto selectedItm;
    private PartDto selectedPart;
    private String orderStatus;
    private ObservableList<OrderDetailsTm> tmList = FXCollections.observableArrayList();
    private double tot = 0;
    private OrderBo orderBo=new OrderBoImpl();
    private double inifee=0.0;

    private OrderDetailsTm selectedUpTm;

    public void initialize() throws SQLException, ClassNotFoundException {

        colId.setCellValueFactory(new TreeItemPropertyValueFactory<>("partId"));
        colName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colDelete.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        Long userId = UserInstanceController.getInstance().getUserId();
        allEmployees = employeeBo.getAll();
        for (EmployeeDto dto: allEmployees) {
            if(dto.getUserId().equals(userId)){
                employeeDto=dto;
            }
        }
        employeeIdLbl.setText("Employee Id : "+employeeDto.getUserId());
        positionStatus=employeeDto.getPosition().equalsIgnoreCase("Admin");
        if(!positionStatus){
            salesReportBtn.setVisible(false);
        }
        selectCustTextField.setEditable(false);
        selectItmTextField.setEditable(false);
        addPartTextField.setEditable(false);
        orderStatusTextField.setEditable(false);
        calculateDate();
        calculateTime();
        loadCustomers();
        loadItems();
        loadStatus();
        loadParts();

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
    private void loadStatus() {
        String [] status={"Pending","Processing","completed"};
        for (String stat:status) {
            MenuItem itm=new MenuItem(stat);
            statusMenuBtn.getItems().add(itm);
            itm.setOnAction(e->{
                orderStatusTextField.setText(itm.getText());
                orderStatus=itm.getText();
            });
        }

    }

    private void loadItems() throws SQLException, ClassNotFoundException {
        List<ItemDto> dtoList = itemBo.getAll();

        for (ItemDto dto:dtoList) {
            MenuItem itm=new MenuItem(dto.getItemId()+"-"+dto.getName());
            itmMenuBtn.getItems().add(itm);
            itm.setOnAction(e->{
                selectItmTextField.setText(itm.getText());
                selectedItm=dto;
                feeTextField.setText(selectedItm.getStandardFee());
                setTotal();
            });
        }
    }

    private void loadCustomers() throws SQLException, ClassNotFoundException {
        List<CustomerDto> dtoList = customerBo.getAll();

        for (CustomerDto dto:dtoList) {
            MenuItem itm=new MenuItem(dto.getCustomerId()+"-"+dto.getName()+"-"+dto.getContact());
            custMenuBtn.getItems().add(itm);
            itm.setOnAction(e->{
                selectCustTextField.setText(itm.getText());
                selectedCustomer=dto;
            });
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

    public void setTotal(){
        if(feeTextField.getText()!=null|| !feeTextField.getText().equals("")){
            if(containsOnlyNumbers(feeTextField.getText(),false)){
                tot+=Double.parseDouble(feeTextField.getText())-inifee;
                inifee=Double.parseDouble(feeTextField.getText());
                lblTotal.setText(String.valueOf(tot));
            }
        }

    }

    public void newCustBtnOnAction(ActionEvent actionEvent) {
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

    public void newItmOnAction(ActionEvent actionEvent) {
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

    private void calculateTime() {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.ZERO,
                actionEvent -> lblTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        ), new KeyFrame(Duration.seconds(1)));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void calculateDate(){
        LocalDate currentDate = LocalDate.now();

        // Define a date formatter to format the date as a string
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the date and print it
        String formattedDate = currentDate.format(dateFormatter);
        lblDate.setText(formattedDate);
    }

    public void updatePartOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(partQtyTextField.getText()!=null && unitPriceTextField.getText()!=null
        && containsOnlyNumbers(partQtyTextField.getText(),true)
                && containsOnlyNumbers(unitPriceTextField.getText(),false) ){

            tot -= selectedUpTm.getPrice();

//
//            JFXButton btn = new JFXButton("Delete");
//            List<PartDto> parts=partBo.getAll();
//            PartDto selectedDto = new PartDto();
//
//            // code from chat gpt
//            String numberString = addPartTextField.getText().replaceAll("[^\\d]", ""); // Remove non-digit characters
//            long extractedNumber = Long.parseLong(numberString);
//            for (PartDto dto:parts) {
//                if(dto.getPartId()==extractedNumber){
//                    selectedDto=dto;
//                }
//            }
//
//            OrderDetailsTm tm = new OrderDetailsTm(
//                    selectedDto.getPartId(),
//                    selectedDto.getName(),
//                    Integer.parseInt(partQtyTextField.getText()),
//                    Double.parseDouble(priceTextField.getText()),
//                    btn
//            );
//
//
//            btn.setOnAction(actionEvent1 -> {
//                tmList.remove(tm);
//                tot -= tm.getPrice();
//                partTbl.refresh();
//                lblTotal.setText(String.format("%.2f",tot));
//            });
//            tmList.add(tm);
//            tot+= tm.getPrice();
//
//            partTbl.refresh();
//            lblTotal.setText(String.format("%.2f",tot));

            JFXButton btn = new JFXButton("Delete");
            List<PartDto> parts=partBo.getAll();
            PartDto selectedDto = new PartDto();
            System.out.println(selectedUpTm.getPartId());
//            String[] splitValues = addPartTextField.getText().split("-");
//            for (String val:splitValues) {
//                System.out.println(val);
//            }
            // code from chat gpt

//            String numberString = addPartTextField.getText().replaceAll("[^\\d]", ""); // Remove non-digit characters
//            System.out.println(addPartTextField.getText().replaceAll("[^\\d]", ""));
            Long extractedNumber = selectedUpTm.getPartId();
            System.out.println(extractedNumber);
            for (PartDto dto:parts) {
                if(dto.getPartId()==extractedNumber){
                    selectedDto=dto;
                }
            }
            System.out.println(selectedDto.getPartId());
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

            clearFields(false);

            selectedUpTm=null;
            clearFields(false);


        }
    }

    public void addPartOnAction(ActionEvent actionEvent) {
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

            clearFields(false);

            selectedPart=null;

        }else if(selectedUpTm!=null){

            new Alert(Alert.AlertType.INFORMATION,"Click Update").show();

        }
    }
    private void clearFields(boolean all) {
        partTbl.refresh();
        selectedPart=null;
        if(all){
            JFXTextField[] textFields = {addPartTextField, partQtyTextField
                    , unitPriceTextField, priceTextField,selectCustTextField
                    ,selectItmTextField,orderStatusTextField,feeTextField};
            for (JFXTextField textField:textFields) {
                textField.clear();
            }
        }else {
            JFXTextField[] textFields = {addPartTextField, partQtyTextField
                    , unitPriceTextField, priceTextField};
            for (JFXTextField textField:textFields) {
                textField.clear();
            }
        }

    }

    public void placeOrderBtnOnAction(ActionEvent actionEvent) throws SQLException {
        if(feeTextField.getText()!=null && containsOnlyNumbers(feeTextField.getText(),false)){
            List<OrderDetailDto> list = new ArrayList<>();
            Long nextId=orderBo.getNextId();
            for (OrderDetailsTm tm:tmList) {
                OrderDetailDto dto=new OrderDetailDto();
                dto.setOrderId(nextId);
                dto.setPartId(tm.getPartId());
                dto.setQuantity(tm.getQty());
                dto.setPrice(tm.getPrice());
                list.add(dto);
            }
//        if (!tmList.isEmpty()){
            boolean isSaved = false;
            try {
                OrderDto dto=new OrderDto();
                dto.setCustomerId(selectedCustomer.getCustomerId());
                dto.setItemId(selectedItm.getItemId());
                dto.setEmployeeId(employeeDto.getUserId());
                dto.setDate(lblDate.getText());
                dto.setOrderStaus(orderStatusTextField.getText());
                dto.setFee(Double.parseDouble(feeTextField.getText()));
                dto.setTotal(Double.parseDouble(lblTotal.getText()));
                dto.setList(list);
                isSaved = orderBo.save(dto);
                if (isSaved){
                    new Alert(Alert.AlertType.INFORMATION,"Orders Saved!").show();
                    clearFields(true);
                    selectedUpTm=null;
                    selectedPart=null;
                    tmList.clear();
                    partTbl.refresh();
                }else{
                    new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    //Gerated Using Chatgpt
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



    public void partQtyKeyReleased(KeyEvent keyEvent) {
        setPrice();
    }


    public void feeKeyReleased(KeyEvent keyEvent) {
        setTotal();
    }

    public void reloadBtnOnAction(ActionEvent actionEvent) {
        clearFields(true);
    }

    public void unitPriceOnKeyReleased(KeyEvent keyEvent) {
        setPrice();
    }
}
