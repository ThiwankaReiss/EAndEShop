package controller;

import bo.custom.EmployeeBo;
import bo.custom.impl.EmployeeBoImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import dto.EmployeeDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

        userIdTxt.setText("User Id : "+userId+" - "+employeeDto.getPosition());
        descriptionTxt.setText(getEmployeeDescrtiption());
        userNameTextField.setText(employeeDto.getName());
        passwordTextField.setText(employeeDto.getPassword());
        emailTextField.setText(employeeDto.getEmail());
        phoneNumberTextField.setText(employeeDto.getContact());

        fieldEditManager(false);
        positionStatus=employeeDto.getPosition().equalsIgnoreCase("Admin");
        System.out.println(positionStatus);
        btnManager(true);



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
        
    }
    public String formatDescription(){
        String description=descriptionTxt.getText();
        String modifiedTxt=  description.replaceAll("[\\n\\s]+", " ");
        return modifiedTxt.replaceAll("Description","");
    }

    public void updateBtnOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        employeeBo.updateEmployee(new EmployeeDto(
                employeeDto.getUserId(),
                emailTextField.getText(),
                employeeDto.getPosition(),
                phoneNumberTextField.getText(),
                userNameTextField.getText(),
                passwordTextField.getText(),
                formatDescription()
        ));
    }

    public void backBtnOnAction(ActionEvent actionEvent) {
        fieldEditManager(false);
        btnManager(true);
    }
}
