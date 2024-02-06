package controller;

import bo.custom.EmployeeBo;
import bo.custom.OrderBo;
import bo.custom.impl.EmployeeBoImpl;
import bo.custom.impl.OrderBoImpl;
import com.jfoenix.controls.JFXTextField;
import dto.EmployeeDto;
import dto.OrderDto;
import dto.PointDto;
import dto.SalesCordinateDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jfree.data.general.DefaultPieDataset;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SalesReportPieChartController {
    public GridPane pane;
    public MenuItem salesReportBtn;
    public JFXTextField startDateTextField;
    public JFXTextField endDateTextField;
    public PieChart pieChart;
    private EmployeeBo employeeBo = new EmployeeBoImpl();
    private List<EmployeeDto> allEmployees;
    private EmployeeDto employeeDto;
    private Boolean positionStatus;
    private OrderBo orderBo=new OrderBoImpl();
    private List<Integer> yearsArray= new ArrayList<>();
    private List<Double> yearlySales= new ArrayList<>();
    private  List<SalesCordinateDto> xyData = new ArrayList<>();
    private String topic;
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

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Category 1", 30);
        dataset.setValue("Category 2", 20);
        dataset.setValue("Category 3", 50);

//
//        pieChart.getData().add(new PieChart.Data("Category 1", 30));
//        pieChart.getData().add(new PieChart.Data("Category 2", 40));
//        pieChart.getData().add(new PieChart.Data("Category 3", 20));


    }


    private void yearlyArray(String startDate,String endDate) throws SQLException, ClassNotFoundException {
        int currentYear=0;
        List<OrderDto> dtoList=orderBo.getAll();
        int i=0;
        for (OrderDto dto:dtoList) {
            if(isDateWithinRange(dto.getDate(),startDate,endDate)){
                if(getYearFromStringDate(dto.getDate())!=currentYear){
                    currentYear=getYearFromStringDate(dto.getDate());
                    yearsArray.add(currentYear);
                    yearlySales.add(dto.getTotal());
                    i++;
                }else {
                    yearlySales.set(i-1,yearlySales.get(i-1)+ dto.getTotal());
                }
            }
        }

    }

    private List<PointDto> dailyArray(String startDate, String endDate) throws SQLException, ClassNotFoundException {
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



    private double [] monthlyArray(String startDate,String endDate) throws SQLException, ClassNotFoundException {
        List<OrderDto> dtoList=orderBo.getAll();


        double [] monthlySales=new double[12];

        for (OrderDto dto:dtoList) {
            if(isDateWithinRange(dto.getDate(),startDate,endDate)){
                switch (getMonthString(dto.getDate()).toLowerCase()) {

                    case "january":
                        monthlySales[0]+=dto.getTotal();
                        break;
                    case "february":
                        monthlySales[1]+=dto.getTotal();
                        break;
                    case "march":
                        monthlySales[2]+=dto.getTotal();
                        break;
                    case "april":
                        monthlySales[3]+=dto.getTotal();
                        break;
                    case "may":
                        monthlySales[4]+=dto.getTotal();
                        break;
                    case "june":
                        monthlySales[5]+=dto.getTotal();
                        break;
                    case "july":
                        monthlySales[6]+=dto.getTotal();
                        break;
                    case "august":
                        monthlySales[7]+=dto.getTotal();
                        break;
                    case "september":
                        monthlySales[8]+=dto.getTotal();
                        break;
                    case "october":
                        monthlySales[9]+=dto.getTotal();
                        break;
                    case "november":
                        monthlySales[10]+=dto.getTotal();
                        break;
                    case "december":
                        monthlySales[11]+=dto.getTotal();
                        break;
                    default:
                        System.out.println("Invalid month");
                }
            }

        }
        return monthlySales;
    }
    public static boolean isDateWithinRange(String dateToCheck, String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date date = sdf.parse(dateToCheck);
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            return !(date.before(start) || date.after(end));
        } catch (ParseException e) {
            // Handle parsing exception if needed
            e.printStackTrace();
            return false;
        }
    }

    public static String[] generateDateArray(String startDate, String endDate) {

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
    public static int getYearFromStringDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = dateFormat.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the exception according to your needs
            return -1; // Return a default value or handle the error case
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

    public void dailyMenuItmOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        topic="Daily Sales Report";
        pieChart.getData().clear();
        xyData.clear();
        if(isDateInputCorrect()){
            List<PointDto> d= dailyArray(startDateTextField.getText(),endDateTextField.getText());
            String[] dates=generateDateArray(startDateTextField.getText(),endDateTextField.getText());

            int i=0;
            for (PointDto dt:d) {

                xyData.add(new SalesCordinateDto(dt.getY(),dates[i]));
                pieChart.getData().add(new PieChart.Data(dates[i], dt.getY()));
                i++;
            }

//            salesReportLineChart.getData().add(series);
        }

    }



    public void monthlyMenuItemOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        topic="Monthly Sales Report";
        pieChart.getData().clear();
        xyData.clear();
        if(isDateInputCorrect()){
            double [] monthlySalesArray= monthlyArray(startDateTextField.getText(),endDateTextField.getText());
            String[] months={"January","February","March","April","May","June","July","August","September","October","November","December"};
//            XYChart.Series<String, Number> series = new XYChart.Series<>();
            int i=0;
            for (double monthlySales:monthlySalesArray) {
//                series.getData().add(new XYChart.Data<>(months[i], monthlySales));
                pieChart.getData().add(new PieChart.Data(months[i], monthlySales));
                xyData.add(new SalesCordinateDto(monthlySales,months[i]));
                i++;
            }
//
//            salesReportLineChart.getData().add(series);
        }
    }

    public void yearlyMenuItemOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        topic="Yearly Sales Report";
        pieChart.getData().clear();
        xyData.clear();
        if(isDateInputCorrect()){
            yearlyArray(startDateTextField.getText(),endDateTextField.getText());

//            XYChart.Series<String, Number> series = new XYChart.Series<>();
            int i=0;
            for (double yearSale:yearlySales) {
//                series.getData().add(new XYChart.Data<>(yearsArray.get(i)+"", yearSale));
                xyData.add(new SalesCordinateDto(yearSale,yearsArray.get(i)+""));
                pieChart.getData().add(new PieChart.Data(yearsArray.get(i)+"", yearSale));
                i++;
            }

//            salesReportLineChart.getData().add(series);
        }
    }

    public void printBtnOnAction(ActionEvent actionEvent) throws JRException, IOException {
        if(isDateInputCorrect()){
            JasperDesign design= JRXmlLoader.load("src/main/resources/reports/EandEShopPieChart.jrxml");


            Map<String, Object> parameters = new HashMap<>();
            parameters.put("Chart_Dataset", xyData);
            parameters.put("ReportName", topic);
            JasperReport jasperReport= JasperCompileManager.compileReport(design);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            JasperViewer.viewReport(jasperPrint,false);
//        OutputStream output = new FileOutputStream(new File("src/main/resources/reports/demo_report.pdf"));
//        JasperExportManager.exportReportToPdfStream(jasperPrint, output);
//        output.close();

        }



    }

    public boolean isDateInputCorrect(){
        if(!isTextFieldNotNull(startDateTextField)){
            new Alert(Alert.AlertType.ERROR,"Enter Start Date").show();
        }else if(!isTextFieldNotNull(endDateTextField)){
            new Alert(Alert.AlertType.ERROR,"Enter End Date").show();
        }else if(!isValidDate(startDateTextField.getText())){
            new Alert(Alert.AlertType.ERROR,"Enter Valid Start Date").show();
        }else if(!isValidDate(endDateTextField.getText())){
            new Alert(Alert.AlertType.ERROR,"Enter Valid End Date").show();
        }else if(!isValidDateRange(startDateTextField.getText(),endDateTextField.getText())){
            new Alert(Alert.AlertType.ERROR,"Start date must be before end date").show();
        }else{
            return true;
        }
        return false;
    }

    public boolean isTextFieldNotNull(JFXTextField textField){
        if(textField.getText().equals(null)||textField.getText().equals("")){
            return false;
        }
        return true;
    }

    public static boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {
            Date date = sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isValidDateRange(String startDateStr, String endDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            // Check if end date is after start date
            if (endDate.after(startDate)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false; // If parsing fails, return false
        }
    }

    public static String getMonthString(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(dateString);
            DateFormat monthFormat = new SimpleDateFormat("MMMM");
            return monthFormat.format(date).toLowerCase(); // Convert to lowercase as per your request
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the ParseException as needed
            return "Invalid Date";
        }
    }

    public void lineChartBtnOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/SalesReportForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Customers Report Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
