package dto;


public class SalesCordinateDto {
    public double getyValue() {
        return yValue;
    }

    public void setyValue(double yValue) {
        this.yValue = yValue;
    }

    public String getxValue() {
        return xValue;
    }

    public void setxValue(String xValue) {
        this.xValue = xValue;
    }

    public SalesCordinateDto(double yValue, String xValue) {
        this.yValue = yValue;
        this.xValue = xValue;
    }



    private double yValue;
    private String xValue;

    public SalesCordinateDto() {
    }
}
