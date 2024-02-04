package controller;

import bo.custom.EmployeeBo;
import bo.custom.OrderBo;
import bo.custom.impl.EmployeeBoImpl;
import bo.custom.impl.OrderBoImpl;
import dto.EmployeeDto;
import dto.OrderDto;
import dto.PointDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SalesReportFormController {
    public GridPane pane;
    public MenuItem salesReportBtn;
    public LineChart salesReportLineChart;
    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;
    private EmployeeDto employeeDto;
    private Boolean positionStatus;
    private OrderBo orderBo=new OrderBoImpl();
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



    }

    public List<PointDto> generateReportArray(String interval,String startDate,String endDate) throws SQLException, ClassNotFoundException {
        double [] reportArray=new double[0];
        switch (interval){
            case "daily": return dailyArray(startDate,endDate);
            case "monthly":return monthlyArray(startDate,endDate);
            case "yearly":return yearlyArray(startDate,endDate);
        }
        return null;
    }

    private List<PointDto> yearlyArray(String startDate,String endDate) throws SQLException, ClassNotFoundException {

        return null;
    }

    private List<PointDto> dailyArray(String startDate,String endDate) throws SQLException, ClassNotFoundException {
        List<PointDto> pointDtos=new ArrayList<>();
        String[] dates=generateDateArray(startDate,endDate);
        List<OrderDto> dtoList=orderBo.getAll();
        boolean foundFistDate=false;
        int k=0;
        double i=0;
        for (String date:dates) {

            double dailySale=0;
            int j=0;
            if(!foundFistDate){
                L1:for (OrderDto dto:dtoList) {
                    if(date.equals(dto.getDate())){
                        dailySale+=dto.getTotal();
                        foundFistDate=true;

                    }else if(!date.equals(dto.getDate()) && foundFistDate){
                        k=j;
                        break L1;
                    }
                    j++;
                }
            }else{

                if(k<dtoList.size() && !dtoList.get(k).getDate().equals(date)){
                    ++k;
                }
                while( k<dtoList.size() && dtoList.get(k).getDate().equals(date)){
                    dailySale+=dtoList.get(k).getTotal();
                    ++k;
                }
            }


            pointDtos.add(new PointDto(i,dailySale));

            i+=1;
        }
        return pointDtos;
    }

    private List<PointDto> monthlyArray(String startDate,String endDate) {
        return null;
    }

    public static String[] generateDateArray(String startDate, String endDate) {
        System.out.println("Enterd date generation");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        List<String> dateList = new ArrayList<>();

        while (!start.isAfter(end)) {
            dateList.add(start.format(formatter));
            start = start.plusDays(1);
        }
        System.out.println("Exit date generation");
        return dateList.toArray(new String[0]);
    }
    public void addDataPoint(String label, Number value) {

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

    public void dailyMenuItmOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        List<PointDto> d= dailyArray("2024-01-20","2024-02-04");
        String[] dates=generateDateArray("2024-01-20","2024-02-04");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        int i=0;
        for (PointDto dt:d) {
            series.getData().add(new XYChart.Data<>(dates[i], dt.getY()));
            i++;
        }
        salesReportLineChart.getData().add(series);
    }

    public void monthlyMenuItemOnAction(ActionEvent actionEvent) {
    }

    public void yearlyMenuItemOnAction(ActionEvent actionEvent) {
    }

    public void printBtnOnAction(ActionEvent actionEvent) {

    }
}
