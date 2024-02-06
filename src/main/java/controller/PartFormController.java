package controller;

import bo.custom.EmployeeBo;
import bo.custom.PartBo;
import bo.custom.impl.EmployeeBoImpl;
import bo.custom.impl.PartBoImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.EmployeeDto;
import dto.PartDto;
import dto.tm.PartTm;
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
import java.util.regex.Pattern;

public class PartFormController {
    public GridPane pane;
    public MenuItem salesReportBtn;
    public JFXTextField nameTextField;
    public JFXTextField categoryTextField;
    public JFXTextField standardFeeTextField;
    public JFXTextField partIdTextField;
    public Label lblDate;
    public Label lblTime;
    public JFXTreeTableView <PartTm> partTbl;
    public TreeTableColumn colId;
    public TreeTableColumn colName;
    public TreeTableColumn colCategory;
    public TreeTableColumn colFee;
    public TreeTableColumn colDelete;
    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;
    private EmployeeDto employeeDto;
    private Boolean positionStatus;
    private PartTm selectedPart;
    private PartBo partBo = new PartBoImpl();

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

        partTbl.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                setData(newValue.getValue());
            }catch (NullPointerException e){

            }
        });

        colId.setCellValueFactory(new TreeItemPropertyValueFactory<>("partId"));
        colName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        colFee.setCellValueFactory(new TreeItemPropertyValueFactory<>("standardFee"));
        colCategory.setCellValueFactory(new TreeItemPropertyValueFactory<>("category"));
        colDelete.setCellValueFactory(new TreeItemPropertyValueFactory<>("deleteBtn"));
        loadTable();
        partIdTextField.setEditable(false);
        partIdTextField.setText(String.valueOf(partBo.getNextId()));
        selectedPart=new PartTm();
        selectedPart.setPartId(partBo.getNextId());

        calculateTime();
        calculateDate();

    }
    private void setData(PartTm newValue) {
        if (newValue != null) {
            partIdTextField.setText(String.valueOf(newValue.getPartId()));
            nameTextField.setText(newValue.getName());
            categoryTextField.setText(newValue.getCategory());
            standardFeeTextField.setText(newValue.getStandardFee());
            selectedPart=newValue;
        }
    }
    private void loadTable() {
        ObservableList<PartTm> tmList = FXCollections.observableArrayList();

        try {
            List<PartDto> dtoList = partBo.getAll();

            for (PartDto dto:dtoList) {
                JFXButton btn = new JFXButton("Delete");

                PartTm c = new PartTm(
                        dto.getPartId(),
                        dto.getName(),
                        dto.getCategory(),
                        dto.getStandardFee(),
                        btn
                );

                btn.setStyle("-fx-background-color: white");
                btn.setOnAction(actionEvent -> {
                    deletePart(c.getPartId());
                });

                tmList.add(c);
            }
            try {
                TreeItem<PartTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
                partTbl.setRoot(treeItem);
                partTbl.setShowRoot(false);
            }catch (Exception e){

            }

            TreeTableColumn[] t={colId,colDelete,colCategory,colFee,colName};
            for (TreeTableColumn col:t) {
                col.setStyle("-fx-text-fill: white;-fx-background-color :  rgba(74, 101, 114, 1);");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deletePart(Long partId) {
        try {
            boolean isDeleted = partBo.delete(partId);
            if (isDeleted){
                new Alert(Alert.AlertType.INFORMATION,"Part Deleted!").show();
                loadTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void addOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(isValidUser(false)){
            PartDto dto=new PartDto();
            dto.setName(nameTextField.getText());
            dto.setCategory(categoryTextField.getText());
            dto.setStandardFee(standardFeeTextField.getText());

            boolean isAdded=partBo.save(dto);
            if(isAdded){
                clearFields();
                try {
                    loadTable();
                }catch (Exception e){}

                new Alert(Alert.AlertType.INFORMATION,"Part Added successfully").show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong ").show();
            }

        }

    }

    private void clearFields() throws SQLException {
        partTbl.refresh();
        JFXTextField[] textFields={nameTextField,categoryTextField
                ,standardFeeTextField};
        for (JFXTextField textField:textFields) {
            textField.clear();

        }
        partIdTextField.setText(String.valueOf(partBo.getNextId()));
    }

    private boolean isValidUser(boolean update) throws SQLException, ClassNotFoundException {
        JFXTextField[] textFields={nameTextField
                ,standardFeeTextField};
        for (JFXTextField textField:textFields) {
            if(textField.getText().equals(null)||textField.getText().equals("")){
                textField.setPromptText("Enter "+textField.getPromptText());
                textField.setStyle("-fx-prompt-text-fill:red;-fx-text-fill : white;");
                return false;
            }
        }

        List<PartDto> dtoList = partBo.getAll();
        if(!containsOnlyNumbers(standardFeeTextField.getText())){
            new Alert(Alert.AlertType.ERROR,"Invalid Fee. (Fee can have only numbers)").show();
            return false;
        }
        boolean isUpdatable=false;
        if(update){
            for (PartDto dto:dtoList) {
                if(partIdTextField.getText().equals(String.valueOf(dto.getPartId()))){
                    isUpdatable=true;
                }
            }
            if(!isUpdatable){
                new Alert(Alert.AlertType.ERROR,"Please click add.Part must be added to be updated").show();
                return false;
            }
        }

        for (PartDto dto:dtoList) {
            if(!selectedPart.getPartId().equals(dto.getPartId())||!update) {
                String[] dtoAttributes = {dto.getName(),dto.getStandardFee()};
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

    public void reloadBtnOnAction(ActionEvent actionEvent) throws SQLException {
        clearFields();
    }

    public void updateBtnOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if (isValidUser(true)) {

            PartDto dto = new PartDto(
                    selectedPart.getPartId(),
                    nameTextField.getText(),
                    categoryTextField.getText(),
                    standardFeeTextField.getText()
            );

            boolean isAdded=partBo.update(dto);

            if(isAdded){
                clearFields();
                try {
                    loadTable();
                }catch (Exception e){}

                new Alert(Alert.AlertType.INFORMATION,"Part Updated successfully").show();
            }else{
                new Alert(Alert.AlertType.ERROR, "Part wasn't added successfully ").show();
            }
        }
    }

    //Gerated Using Chatgpt
    public static boolean containsOnlyNumbers(String input) {
        // Define a regular expression pattern for numbers with decimals
        String numericPattern = "^[0-9]+(\\.[0-9]+)?$";

        // Compile the pattern
        Pattern pattern = Pattern.compile(numericPattern);

        // Check if the input matches the pattern
        return pattern.matcher(input).matches();
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
