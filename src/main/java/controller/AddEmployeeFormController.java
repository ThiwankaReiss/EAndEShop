package controller;

import bo.custom.EmployeeBo;
import bo.custom.impl.EmployeeBoImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.EmployeeDto;
import dto.tm.EmployeeTm;
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
import javafx.scene.input.MouseEvent;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddEmployeeFormController {
    public GridPane pane;
    public MenuItem salesReportBtn;
    public TreeTableColumn colId;
    public TreeTableColumn colName;
    public TreeTableColumn colContact;
    public TreeTableColumn colEmail;
    public TreeTableColumn colPosition;
    public TreeTableColumn colDelete;
    public Label lblDate;
    public Label lblTime;
    public JFXTreeTableView<EmployeeTm> employeeTbl;
    public JFXTextField nameTextField;
    public JFXTextField contactTextField;
    public JFXTextField emailTextField;
    public JFXTextField positionTextField;
    public JFXTextField passwordTextField;
    public MenuButton positionMenuBtn;
    public TreeTableColumn colPassword;

    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;

    private EmployeeDto employeeDto;

    private Boolean positionStatus;
    private Long SelctedUser=null;

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
        calculateDate();
        calculateTime();
        colId.setCellValueFactory(new TreeItemPropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new TreeItemPropertyValueFactory<>("contact"));
        colEmail.setCellValueFactory(new TreeItemPropertyValueFactory<>("email"));
        colPosition.setCellValueFactory(new TreeItemPropertyValueFactory<>("position"));
        colPassword.setCellValueFactory(new TreeItemPropertyValueFactory<>("password"));
        colDelete.setCellValueFactory(new TreeItemPropertyValueFactory<>("deleteBtn"));

        loadTable();

        employeeTbl.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                setData(newValue.getValue());
            }catch (NullPointerException e){

            }
        });
        addMenuItems();



    }


    private void setData(EmployeeTm newValue) {
        if (newValue != null) {
            contactTextField.setText(newValue.getContact());
            passwordTextField.setText(newValue.getPassword());
            nameTextField.setText(newValue.getName());
            emailTextField.setText(newValue.getEmail());
            positionTextField.setText(newValue.getPosition());
            SelctedUser=newValue.getUserId();
        }
    }

    private void loadTable() {
        ObservableList<EmployeeTm> tmList = FXCollections.observableArrayList();

        try {
            List<EmployeeDto> dtoList = employeeBo.getAll();

            for (EmployeeDto dto:dtoList) {
                JFXButton btn = new JFXButton("Delete");

                EmployeeTm c = new EmployeeTm(
                        dto.getUserId(),
                        dto.getName(),
                        dto.getContact(),
                        dto.getEmail(),
                        dto.getPosition(),
                        dto.getPassword(),
                        btn
                );

                btn.setStyle("-fx-background-color: white");
                btn.setOnAction(actionEvent -> {
                    deleteEmployee(c.getUserId());
                });

                tmList.add(c);
            }
            try {
            TreeItem<EmployeeTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);

                employeeTbl.setRoot(treeItem);
                employeeTbl.setShowRoot(false);
            }catch (Exception e){

            }

            TreeTableColumn [] t={colId,colDelete,colEmail,colPosition,colContact,colName,colPassword};
            for (TreeTableColumn col:t) {
                col.setStyle("-fx-text-fill: white;-fx-background-color :  rgba(74, 101, 114, 1);");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void deleteEmployee(Long id) {
        if(!id.equals(1)){
            try {
                boolean isDeleted = employeeBo.delete(id);
                if (isDeleted){
                    new Alert(Alert.AlertType.INFORMATION,"Employee Deleted!").show();
                    loadTable();
                }else{
                    new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
                }

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }else{
            new Alert(Alert.AlertType.ERROR,"System generated admin user cannot be deleted").show();
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
        if(isValidUpdatableUser()){
            List<EmployeeDto> dtoList = employeeBo.getAll();
            EmployeeDto dto = null;
            for (EmployeeDto employee:dtoList) {
                if(SelctedUser.equals(employee.getUserId())){
                    dto=employee;
                }
            }

            dto.setName(nameTextField.getText());
            dto.setContact(contactTextField.getText());
            dto.setEmail(emailTextField.getText());
            dto.setPassword(passwordTextField.getText());
            dto.setPosition(positionTextField.getText());
            boolean isAdded=employeeBo.update(dto);

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
        addMenuItems();

    }

    public void reloadBtnOnAction(ActionEvent actionEvent) {

        clearFields();
    }

    public void addOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        if(isValidUser()){
            EmployeeDto dto=new EmployeeDto();
            dto.setName(nameTextField.getText());
            dto.setContact(contactTextField.getText());
            dto.setEmail(emailTextField.getText());
            dto.setPassword(passwordTextField.getText());
            dto.setPosition(positionTextField.getText());
            dto.setDescription("Click edit profile button to add a description about you");
            boolean isAdded=employeeBo.save(dto);
            if(isAdded){
                clearFields();
                try {
                    loadTable();
                }catch (Exception e){}

                new Alert(Alert.AlertType.INFORMATION,"Employee Added successfully").show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Employee ").show();
            }

        }
        addMenuItems();

    }


    private void clearFields() {
        employeeTbl.refresh();
        SelctedUser=null;
        JFXTextField [] textFields={nameTextField,emailTextField
                ,contactTextField,passwordTextField,positionTextField};
        for (JFXTextField textField:textFields) {
            textField.clear();
        }
    }
    private boolean isValidUpdatableUser() throws SQLException, ClassNotFoundException {
        JFXTextField [] textFields={nameTextField,emailTextField
                ,contactTextField,passwordTextField,positionTextField};
        for (JFXTextField textField:textFields) {
            if(textField.getText().equals(null)||textField.getText().equals("")){
                textField.setPromptText("Enter "+textField.getPromptText());
                textField.setStyle("-fx-prompt-text-fill:red;-fx-text-fill : white;");
                return false;
            }
        }

        List<EmployeeDto> dtoList = employeeBo.getAll();
        if(!validatePhoneNumber(contactTextField.getText())){
            return false;
        }
        if(!isValidEmail(emailTextField.getText())){
            new Alert(Alert.AlertType.ERROR,"Invalid email.").show();
            return false;
        }

        boolean isUpdatable=false;

        for (EmployeeDto dto:dtoList) {
            if(SelctedUser != null){
                isUpdatable=true;
            }

        }
        if(!isUpdatable){
            new Alert(Alert.AlertType.ERROR,"Please click add.Employee must be added to be updated").show();
            return false;
        }


        for (EmployeeDto dto:dtoList) {
            if(!SelctedUser.equals(dto.getUserId())){
                String[] dtoAttributes={dto.getName(),
                        dto.getEmail(),dto.getContact(),dto.getPassword()};
                for (int i = 0; i < textFields.length-1; i++) {
                    if(textFields[i].getText().equals(dtoAttributes[i])){
                        new Alert(Alert.AlertType.ERROR,"This "+textFields[i].getPromptText()+" is already taken. Enter different "+textFields[i].getPromptText()).show();
                        return false;
                    }
                }
            }

        }

        return true;
    }

    private boolean isValidUser() throws SQLException, ClassNotFoundException {
        JFXTextField [] textFields={nameTextField,emailTextField
                ,contactTextField,passwordTextField,positionTextField};
        for (JFXTextField textField:textFields) {
            if(textField.getText().equals(null)||textField.getText().equals("")){
                textField.setPromptText("Enter "+textField.getPromptText());
                textField.setStyle("-fx-prompt-text-fill:red;-fx-text-fill : white;");
                return false;
            }
        }

        List<EmployeeDto> dtoList = employeeBo.getAll();

        if(!validatePhoneNumber(contactTextField.getText())){
            return false;
        }
        if(!isValidEmail(emailTextField.getText())){
            new Alert(Alert.AlertType.ERROR,"Invalid email.").show();
            return false;
        }

        for (EmployeeDto dto:dtoList) {
            String[] dtoAttributes={dto.getName(),
            dto.getEmail(),dto.getContact(),dto.getPassword()};
            for (int i = 0; i < textFields.length-1; i++) {
                if(textFields[i].getText().equals(dtoAttributes[i])){
                    new Alert(Alert.AlertType.ERROR,"This "+textFields[i].getPromptText()+" is already taken. Enter different "+textFields[i].getPromptText()).show();
                    return false;
                }
            }
        }

        return true;
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

    public void addMenuItems() throws SQLException, ClassNotFoundException {
        List<EmployeeDto> dtoList = employeeBo.getAll();
        ArrayList <String> positions=new ArrayList<>();

        for (EmployeeDto dto:dtoList) {
            Boolean present=false ;
            for (String position:positions) {
                if (position.equals(dto.getPosition())){
                    present=true;
                }
            }
            if(!present){
                positions.add(dto.getPosition());
            }
        }

        ObservableList<MenuItem> items=positionMenuBtn.getItems();
        for (String position:positions) {
            boolean isNewPosition=true;
            for (MenuItem item:items) {

                if(position.equals(item.getText())){
                    isNewPosition=false;
                }

            }
            if (isNewPosition){
                MenuItem itm=new MenuItem(position);
                positionMenuBtn.getItems().add(itm);
                itm.setOnAction(e->{
                    positionTextField.setText(itm.getText());
                });

            }

        }
    }

    public void positionMenuBtnOnAction(MouseEvent actionEvent) throws SQLException, ClassNotFoundException {

//        List<EmployeeDto> dtoList = employeeBo.allEmployees();
//        ArrayList <String> positions=new ArrayList<>();
//
//        for (EmployeeDto dto:dtoList) {
//            Boolean present=false ;
//            for (String position:positions) {
//                if (position.equals(dto.getPosition())){
//                    present=true;
//                }
//            }
//            if(!present){
//                positions.add(dto.getPosition());
//            }
//        }
//
//        ObservableList<MenuItem> items=positionMenuBtn.getItems();
//        for (String position:positions) {
//            boolean isNewPosition=true;
//            for (MenuItem item:items) {
//
//                if(position.equals(item.getText())){
//                    isNewPosition=false;
//                }
//
//            }
//            if (isNewPosition){
//                MenuItem itm=new MenuItem(position);
//                positionMenuBtn.getItems().add(itm);
//                itm.setOnAction(e->{
//                    positionTextField.setText(itm.getText());
//                });
//
//            }
//
//        }


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

}
