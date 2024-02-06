package controller;

import bo.custom.CustomerBo;
import bo.custom.EmployeeBo;
import bo.custom.impl.CustomerBoImpl;
import bo.custom.impl.EmployeeBoImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.EmployeeDto;
import dto.tm.CustomerTm;
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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerFormController {
    public GridPane pane;
    public MenuItem salesReportBtn;
    
    public TreeTableColumn colId;
    public TreeTableColumn colName;
    public TreeTableColumn colContact;
    public TreeTableColumn colEmail;
    public TreeTableColumn colDelete;
    public JFXTreeTableView<CustomerTm> customerTbl;
    public JFXTextField customerIdTextField;
    public JFXTextField nameTextField;
    public JFXTextField contactTextField;
    public JFXTextField emailTextField;
    public Label lblDate;
    public Label lblTime;
    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;
    private EmployeeDto employeeDto;
    private Boolean positionStatus;
    private CustomerBo customerBo=new CustomerBoImpl();
    private CustomerTm selectedCustomer;
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
        colId.setCellValueFactory(new TreeItemPropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new TreeItemPropertyValueFactory<>("contact"));
        colEmail.setCellValueFactory(new TreeItemPropertyValueFactory<>("email"));
        colDelete.setCellValueFactory(new TreeItemPropertyValueFactory<>("deleteBtn"));

        loadTable();

        customerTbl.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                setData(newValue.getValue());
            }catch (NullPointerException e){

            }
        });
        customerIdTextField.setEditable(false);
        customerIdTextField.setText(String.valueOf(customerBo.getNextId()));
        selectedCustomer=new CustomerTm();
        selectedCustomer.setCustomerId(customerBo.getNextId());
        calculateTime();
        calculateDate();

    }

    private void loadTable() {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();

        try {
            List<CustomerDto> dtoList = customerBo.getAll();

            for (CustomerDto dto:dtoList) {
                JFXButton btn = new JFXButton("Delete");

                CustomerTm c = new CustomerTm(
                        dto.getCustomerId(),
                        dto.getName(),
                        dto.getContact(),
                        dto.getEmail(),
                        btn
                );

                btn.setStyle("-fx-background-color: white");
                btn.setOnAction(actionEvent -> {
                    delteCustomer(c.getCustomerId());
                });

                tmList.add(c);
            }
            try {
                TreeItem<CustomerTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);

                customerTbl.setRoot(treeItem);
                customerTbl.setShowRoot(false);
            }catch (Exception e){

            }

            TreeTableColumn [] t={colId,colDelete,colEmail,colContact,colName};
            for (TreeTableColumn col:t) {
                col.setStyle("-fx-text-fill: white;-fx-background-color :  rgba(74, 101, 114, 1);");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void delteCustomer(Long customerId) {

        try {
            boolean isDeleted = customerBo.delete(customerId);
            if (isDeleted){
                new Alert(Alert.AlertType.INFORMATION,"Customer Deleted!").show();
                loadTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    private void setData(CustomerTm newValue) {
        if (newValue != null) {
            customerIdTextField.setText(String.valueOf(newValue.getCustomerId()));
            nameTextField.setText(newValue.getName());
            emailTextField.setText(newValue.getEmail());
            contactTextField.setText(newValue.getContact());
            selectedCustomer=newValue;
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

    public void updateBtnOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if (isValidUser(true)) {

            CustomerDto dto = new CustomerDto(
                    selectedCustomer.getCustomerId(),
                    nameTextField.getText(),
                    contactTextField.getText(),
                    emailTextField.getText()
            );

            boolean isAdded=customerBo.update(dto);

            if(isAdded){
                clearFields();
                try {
                    loadTable();
                }catch (Exception e){}

                new Alert(Alert.AlertType.INFORMATION,"Employee Updated successfully").show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Employee ").show();
            }
        }
    }

    public void reloadBtnOnAction(ActionEvent actionEvent) throws SQLException {
        clearFields();
    }

    public void addOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(isValidUser(false)){
            CustomerDto dto=new CustomerDto();
            dto.setName(nameTextField.getText());
            dto.setContact(contactTextField.getText());
            dto.setEmail(emailTextField.getText());

            boolean isAdded=customerBo.save(dto);
            if(isAdded){
                clearFields();
                try {
                    loadTable();
                }catch (Exception e){}

                new Alert(Alert.AlertType.INFORMATION,"Customer Added successfully").show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong ").show();
            }

        }

    }

    private void clearFields() throws SQLException {
        customerTbl.refresh();
        JFXTextField [] textFields={nameTextField,emailTextField
                ,contactTextField};
        for (JFXTextField textField:textFields) {
            textField.clear();

        }
        customerIdTextField.setText(String.valueOf(customerBo.getNextId()));
    }

    private boolean isValidUser(boolean update) throws SQLException, ClassNotFoundException {
        JFXTextField [] textFields={nameTextField,emailTextField
                ,contactTextField};
        for (JFXTextField textField:textFields) {
            if(textField.getText().equals(null)||textField.getText().equals("")){
                textField.setPromptText("Enter "+textField.getPromptText());
                textField.setStyle("-fx-prompt-text-fill:red;-fx-text-fill : white;");
                return false;
            }
        }

        List<CustomerDto> dtoList = customerBo.getAll();
        if(!validatePhoneNumber(contactTextField.getText())){
            return false;
        }
        if(!isValidEmail(emailTextField.getText())){
            new Alert(Alert.AlertType.ERROR,"Invalid email.").show();
            return false;
        }
        boolean isUpdatable=false;
        if(update){
            for (CustomerDto dto:dtoList) {
                if(customerIdTextField.getText().equals(String.valueOf(dto.getCustomerId()))){
                    isUpdatable=true;
                }
            }
            if(!isUpdatable){
                new Alert(Alert.AlertType.ERROR,"Please click add.Customer must be added to be updated").show();
                return false;
            }
        }


        for (CustomerDto dto:dtoList) {
            if(!selectedCustomer.getCustomerId().equals(dto.getCustomerId())||!update) {
                String[] dtoAttributes = {dto.getName(),
                        dto.getEmail(), dto.getContact()};
                for (int i = 0; i < textFields.length; i++) {
                    if (textFields[i].getText().equals(dtoAttributes[i])) {
                        new Alert(Alert.AlertType.ERROR, "This " + textFields[i].getPromptText() + " is already taken. Enter different " + textFields[i].getPromptText()).show();
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean validatePhoneNumber(String phoneNumber) {

        if (phoneNumber != null && phoneNumber.length() == 10) {

            char firstDigit = phoneNumber.charAt(0);
            if (firstDigit == '0') {

                for (int i = 0; i < phoneNumber.length(); i++) {
                    if (!Character.isDigit(phoneNumber.charAt(i))) {
                        new Alert(Alert.AlertType.ERROR,"Phone number can have only digits").show();
                        return false;
                    }
                }
                return true;
            }else{
                new Alert(Alert.AlertType.ERROR,"First number must be zero").show();
                return false;
            }
        }
        new Alert(Alert.AlertType.ERROR,"Phone number must have 10 digits").show();
        return false;
    }

    // Using Chat gpt
    public static boolean isValidEmail(String email) {
        // Define the regular expression for a valid email address
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(emailRegex);

        // Create a matcher object
        Matcher matcher = pattern.matcher(email);

        // Return true if the email matches the pattern, otherwise false
        return matcher.matches();
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
}
