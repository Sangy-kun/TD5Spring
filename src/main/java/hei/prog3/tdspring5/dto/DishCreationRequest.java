package hei.prog3.tdspring5.dto;

public class DishCreationRequest {

    private String name;
    private String category;
    private Double sellingPrice;

    public DishCreationRequest() {
    }

    public DishCreationRequest(String name, String category, Double sellingPrice) {
        this.name = name;
        this.category = category;
        this.sellingPrice = sellingPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}