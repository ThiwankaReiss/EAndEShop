package controller;

import bo.custom.EmployeeBo;
import bo.custom.impl.EmployeeBoImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import dto.EmployeeDto;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmployeeHomeFormController {
    public JFXTextArea descriptionTxt;
    public GridPane employeePane;
    public Label userIdTxt;
    public JFXTextField userNameTextField;
    public JFXTextField passwordTextField;
    public JFXTextField emailTextField;
    public JFXTextField phoneNumberTextField;
    public Label profileTxt;
    public JFXButton editProfileBtn;
    public JFXButton addEmployeeBtn;
    public JFXButton logoutBtn;

    public JFXButton updateBtn;
    public JFXButton backBtn;
    public MenuItem orderReportBtn;
    public MenuItem customerReportBtn;
    public MenuItem salesReportBtn;
    public Label lblTime;
    public Label lblDate;
    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;

    private EmployeeDto employeeDto;

    private Boolean positionStatus;


    public void initialize() throws SQLException, ClassNotFoundException {

        Long userId = UserInstanceController.getInstance().getUserId();
        allEmployees = employeeBo.getAll();
        for (EmployeeDto dto: allEmployees) {
            if(dto.getUserId().equals(userId)){
                employeeDto=dto;
            }
        }

        userIdTxt.setText("User Id : "+userId+" - "+employeeDto.getPosition());
        descriptionTxt.setText(getEmployeeDescrtiption());
        userNameTextField.setText(employeeDto.getName());
        passwordTextField.setText(employeeDto.getPassword());
        emailTextField.setText(employeeDto.getEmail());
        phoneNumberTextField.setText(employeeDto.getContact());

        fieldEditManager(false);
        positionStatus=employeeDto.getPosition().equalsIgnoreCase("Admin");
        btnManager(true);

        if(!positionStatus){
            salesReportBtn.setVisible(false);
        }
        calculateTime();
        calculateDate();
    }

    private String getEmployeeDescrtiption() {

        String inputString = employeeDto.getDescription();
        int lineLength =34;


        String[] words = inputString.split(" ");
        int count=0;

        String output="";

        String [] line=new String[20];
        int i=0;

        for (int j = 0; j < words.length; j++) {
            count+=words[j].length()+1;
            if(count >= lineLength || words.length-1==j){

                if(words.length-1==j){
                    output+=" "+words[j];
                }
                line[i]=output;
                output="";
                count=0;
                i++;
            }
            output+=" "+words[j];
        }

        output="";
        for (String t:line) {
            if(t!=null){
                int num=lineLength- t.length();
                for (int j = 0; j < num; j++) {
                    if(j%2==0){
                        t=" "+t;
                    }else{
                        t+=" ";
                    }
                }
                output+=t+"\n";
            }

        }
        output="\t\t\tDescription\n"+output;
        return output;

    }


    public void logoutBtnOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        UserInstanceController.getInstance().setUserId(null);
        Stage stage = (Stage) employeePane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/LoginForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void editProfileBtnOnAction(ActionEvent actionEvent) {
        fieldEditManager(true);
        btnManager(false);

    }

    public void fieldEditManager(Boolean option){
        JFXTextField[] textFields={userNameTextField,passwordTextField,emailTextField,phoneNumberTextField};
        for (JFXTextField textField:textFields) {
            textField.setEditable(option);
        }
        descriptionTxt.setEditable(option);
    }
    public void btnManager(Boolean option){
        if(positionStatus){
            logoutBtn.setVisible(option);
            editProfileBtn.setVisible(option);
            addEmployeeBtn.setVisible(option);
            updateBtn.setVisible(!option);
            backBtn.setVisible(!option);
        }else{
            logoutBtn.setVisible(option);
            editProfileBtn.setVisible(option);
            addEmployeeBtn.setVisible(false);
            updateBtn.setVisible(!option);
            backBtn.setVisible(!option);
        }

    }



    public void addEmployeeBtnOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) employeePane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/AddEmployeeForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Add Employee Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String formatDescription(){
        String description=descriptionTxt.getText();
        String modifiedTxt=  description.replaceAll("[\\n\\s]+", " ");
        return modifiedTxt.replaceAll("Description","");
    }

    public void updateBtnOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(descriptionTxt.getText().equals("")||descriptionTxt.getText().equals(null)){
            descriptionTxt.setText(employeeDto.getDescription());
        }

        if(isValidUpdatableUser()){
            employeeBo.update(new EmployeeDto(
                    employeeDto.getUserId(),
                    emailTextField.getText(),
                    employeeDto.getPosition(),
                    phoneNumberTextField.getText(),
                    userNameTextField.getText(),
                    passwordTextField.getText(),
                    formatDescription()
            ));
        }
        initialize();
    }

    private boolean isValidUpdatableUser() throws SQLException, ClassNotFoundException {
        JFXTextField [] textFields={userNameTextField,emailTextField
                ,phoneNumberTextField,passwordTextField};
        for (JFXTextField textField:textFields) {
            if(textField.getText().equals(null)||textField.getText().equals("")){
                textField.setPromptText("Enter "+textField.getPromptText());
                textField.setStyle("-fx-prompt-text-fill:red;-fx-text-fill : white;");
                return false;
            }
        }

        List<EmployeeDto> dtoList = employeeBo.getAll();

        for (EmployeeDto dto:dtoList) {
            if(!employeeDto.getUserId().equals(dto.getUserId())){
                String[] dtoAttributes={dto.getName(),
                        dto.getEmail(),dto.getContact(),dto.getPassword()};
                for (int i = 0; i < textFields.length; i++) {
                    if(textFields[i].getText().equals(dtoAttributes[i])){
                        new Alert(Alert.AlertType.ERROR,"This "+textFields[i].getPromptText()+" is already taken. Enter different "+textFields[i].getPromptText()).show();
                        return false;
                    }
                }
            }

        }

        return true;
    }

    public void backBtnOnAction(ActionEvent actionEvent) {
        fieldEditManager(false);
        btnManager(true);
    }

    public void orderReportBtnOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) employeePane.getScene().getWindow();
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
        Stage stage = (Stage) employeePane.getScene().getWindow();
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
        Stage stage = (Stage) employeePane.getScene().getWindow();
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
        Stage stage = (Stage) employeePane.getScene().getWindow();
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
        Stage stage = (Stage) employeePane.getScene().getWindow();
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
        Stage stage = (Stage) employeePane.getScene().getWindow();
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
        Stage stage = (Stage) employeePane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/OrderForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Orders Form");
            stage.show();
        } catch (Exception e) {

        }
    }

    public void itemsOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) employeePane.getScene().getWindow();
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
        Stage stage = (Stage) employeePane.getScene().getWindow();
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

    public void descriptionTxtMouseClicked(MouseEvent mouseEvent) {
        if(formatDescription().replaceAll("[\\n\\s]+", "").equals(employeeDto.getDescription().replaceAll("[\\n\\s]+", ""))){
            descriptionTxt.setText("");
        }
    }
}
