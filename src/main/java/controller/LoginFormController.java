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
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class LoginFormController {

    public JFXTextField userNameTextField;
    public JFXTextField passwordTextField;
    public GridPane pane;
    public Label loginTxt;
    public JFXButton signInBtn;
    public JFXButton backBtn;
    public Label lblTime;
    public Label lblDate;


    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;
    public GridPane welcomePane;

    public void initialize() throws SQLException, ClassNotFoundException {
        //-------------UI--------------
        welcomePane.setFocusTraversable(false);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setOffsetX(0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        welcomePane.setEffect(dropShadow);
        welcomePane.setCache(true);

        backBtn.setVisible(false);
        saveAdmin();
        calculateTime();
        calculateDate();

    }

    private void saveAdmin() throws SQLException, ClassNotFoundException {
        allEmployees = employeeBo.allEmployees();
        boolean isSaved=false;
        for (EmployeeDto dto:allEmployees) {
            if(dto.getUserId().equals("1")){
                isSaved=true;
            }
        }
        if(!isSaved){
            employeeBo.saveEmployee(
                    new EmployeeDto(
                            "1",
                            "Thiwankar2003@gmail.com",
                            "Admin",
                            "0772469072",
                            "Thiwanka",
                            "ThiwankaReiss",
                            "Hi I'm Thiwanka .I'm the System administrator of E and E Shop. I am highly experience in this industry. "

                    ));
        }
    }

    public void signInBtnOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(signInBtn.getText().equalsIgnoreCase("Sign In")){
            allEmployees = employeeBo.allEmployees();
            boolean sendMessage=true;
            for (EmployeeDto dto: allEmployees) {
                if(dto.getName().equals(
                        userNameTextField.getText()) &&
                        dto.getPassword().equals(
                                passwordTextField.getText()
                        )
                ){
                    openHomePage(dto.getUserId());
                    sendMessage=false;
                }
            }
            if(sendMessage){
                new Alert(Alert.AlertType.ERROR,"Enter User Name and Password Correctly").show();
            }
        } else if (signInBtn.getText().equalsIgnoreCase("Send OTP")) {
            System.out.println("shit");
            sendEmail();
        }


    }

    private void sendEmail() {
        // Sender's email address and password
        String senderEmail = "thiwankar2003@gmail.com";
        String senderPassword = "Thiwanka2003";

        // Recipient's email address
        String recipientEmail = "prelanr@gmail.com";

//        String SSL_FACTORY="javax.net.ssl.SSLSocketFactory";
//        String host ="localhost";
//        String port ="25";
        // Set up properties for the mail server
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

//        properties.setProperty("mail.smtp.host","smtp.gmail.com");
//        properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
//        properties.setProperty("mail.smtp.socketFactory.fallback","false");
//        properties.setProperty("mail.smtp.port","465");
//        properties.setProperty("mail.smtp.socketFactory.port","587");
//
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.debug", "true");
//        properties.put("mail.store.protocol", "pop3");
//        properties.put("mail.transport.protocol", "smtp");

        // Create a session with the specified properties
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);

            // Set the sender's email address
            message.setFrom(new InternetAddress(senderEmail));

            // Set the recipient's email address
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            // Set the subject and text of the email
            message.setSubject("Test Email from Java");
            message.setText("Hello, this is a test email sent from Java.");

            // Send the email
            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void openHomePage(String userId) throws SQLException, ClassNotFoundException {
        UserInstanceController.getInstance().setUserId(userId);
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/EmployeeHomeForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Empoloyee Home"+userId);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forgotPassOnAction(ActionEvent actionEvent) {
        loginTxt.setText("GET OTP");
        backBtn.setVisible(true);
        passwordTextField.setVisible(false);
        signInBtn.setText("Send OTP");

    }

    public void backBtnOnAction(ActionEvent actionEvent) {
        loginTxt.setText("Login");
        backBtn.setVisible(false);
        passwordTextField.setVisible(true);
        signInBtn.setText("Sign In");
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
