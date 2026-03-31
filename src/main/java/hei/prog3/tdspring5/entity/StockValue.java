package hei.prog3.tdspring5.entity;

public class StockValue {
    private Unit unit;
    private double stock;

    public StockValue() {
    }

    public StockValue(Unit unit, double stock) {
        this.unit = unit;
        this.stock = stock;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }
}
