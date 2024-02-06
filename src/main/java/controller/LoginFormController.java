package controller;

import bo.custom.EmployeeBo;
import bo.custom.impl.EmployeeBoImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dto.EmployeeDto;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.mail.*;
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
    public Label otpContDown;
    public JFXButton resendBtn;
    public Hyperlink forgotPasswordTxt;


    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;
    public GridPane welcomePane;

    private EmployeeDto otpDto;
    private static String otp;
    private int durationSeconds = 60;
    private int currentSecond = durationSeconds;
    private boolean screenChanged=false;


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
        resendBtn.setVisible(false);

        backBtn.setVisible(false);
        saveAdmin();
        calculateTime();
        calculateDate();



    }

    private void saveAdmin() throws SQLException, ClassNotFoundException {

        allEmployees = employeeBo.getAll();

        boolean isSaved=false;
        for (EmployeeDto dto:allEmployees) {

            isSaved=true;
            break;
        }
        if(!isSaved){
            EmployeeDto dto=new EmployeeDto();
            dto.setName("Thiwanka");
            dto.setContact("0772469072");
            dto.setEmail("thiwankar2003@gmail.com");
            dto.setPassword("ThiwankaReiss");
            dto.setPosition("Admin");
            dto.setDescription("Hi I'm Thiwanka .I'm the System administrator of E and E Shop. I am highly experience in this industry. ");
            employeeBo.save(dto);
        }
    }

    public void signInBtnOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(signInBtn.getText().equalsIgnoreCase("Sign In")){
            allEmployees = employeeBo.getAll();
            boolean sendMessage=true;
            for (EmployeeDto dto: allEmployees) {
                if((dto.getName().equals(userNameTextField.getText()) &&
                        dto.getPassword().equals(passwordTextField.getText())
                        || (passwordTextField.getText().equals(otp) &&
                        dto.getName().equals(userNameTextField.getText()))
                        )
                ){
                    openHomePage(dto.getUserId());
                    sendMessage=false;
                }
            }
            if(sendMessage){
                if(screenChanged){
                    new Alert(Alert.AlertType.ERROR,"Enter User Name and OTP Correctly").show();
                }else{
                    new Alert(Alert.AlertType.ERROR,"Enter User Name and Password Correctly").show();
                }

            }
        } else if (signInBtn.getText().equalsIgnoreCase("Send OTP")) {
            if(!isAUser().equals("NotAUser")){
                backBtn.setVisible(false);
                otp=getOTP();
                sendEmail(otpDto.getEmail());

            }else{
                new Alert(Alert.AlertType.ERROR,"Incorrect User Name").show();
            }

        }


    }

    private String isAUser() throws SQLException, ClassNotFoundException {
        String userName=userNameTextField.getText();
        if(!(userName.equals(null) || userName.equals(""))){
            allEmployees = employeeBo.getAll();
            for (EmployeeDto dto:allEmployees) {
                if(dto.getName().equals(userName)){
                    otpDto=dto;
                    return dto.getEmail();
                }
            }
        }
        return "NotAUser";
    }

    private String getOTP(){
        int b = (int)(Math.random()*(90000-10000+1)+10000);

        otp=b+"";
        return b+"";
    }


    public void startCountdownTimer(Label label) {


        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentSecond--;
                label.setText("OTP Expires In :"+String.valueOf(currentSecond)+"s");

                if (currentSecond <= 0) {
                    timeline.stop();
                    label.setText("OTP Expired");
                    resendBtn.setVisible(false);
                    otp="OtpTimeOut";
                    signInBtn.setText("Send OTP");
                    otpContDown.setText("");
                    passwordTextField.setVisible(false);
                    backBtn.setVisible(true);

                }
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }



    private void sendEmail(String email) {
        // Sender's email address and password
        String senderEmail = "prelanr@gmail.com";
        String senderPassword = "ibmn kdvy pobz wuvx ";

        // Recipient's email address
        String recipientEmail = email;

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");


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
            message.setSubject("Email from E and E Shop");

            message.setText("Hello "+otpDto.getName()+" ,\n \t Your one time password  is "+otp+"\nThis OTP is valid for 30 seconds");

            // Send the email
            Transport.send(message);
            passwordTextField.setPromptText("OTP");
            passwordTextField.setVisible(true);
            resendBtn.setVisible(true);
            signInBtn.setText("Sign In");
//            scheduleSendMessage(30);
            currentSecond=30;
            startCountdownTimer(otpContDown);



        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void openHomePage(Long userId) throws SQLException, ClassNotFoundException {
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
        forgotPasswordTxt.setVisible(false);
        screenChanged=true;

    }

    public void backBtnOnAction(ActionEvent actionEvent) {
        loginTxt.setText("Login");
        backBtn.setVisible(false);
        passwordTextField.setPromptText("Password");
        passwordTextField.setVisible(true);
        signInBtn.setText("Sign In");
        forgotPasswordTxt.setVisible(true);
        screenChanged=false;
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

    public void resendBtnOnAction(ActionEvent actionEvent) {
        sendEmail(otpDto.getEmail());
    }
}
