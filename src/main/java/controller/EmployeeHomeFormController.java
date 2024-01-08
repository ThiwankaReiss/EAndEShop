package controller;

import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class EmployeeHomeFormController {

    public JFXTextArea descriptionTxt;
    public GridPane employeePane;

    public void initialize(){
        Stage stage = (Stage) employeePane.getScene().getWindow();


        String title=stage.getTitle();

        String[] user = title.split(" ");
        System.out.println(user[user.length-1]);

        String inputString = "My name Is Thiwanka Im a learning to become a software Engineer.";
        int lineLength = 44;


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
            System.out.println(output);
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
        descriptionTxt.setText(output);



    }

    public void logoutBtnOnAction(ActionEvent actionEvent) {

    }
}
