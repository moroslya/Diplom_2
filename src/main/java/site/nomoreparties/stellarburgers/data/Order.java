package site.nomoreparties.stellarburgers.data;

import java.util.List;

public class Order {

    private String id;
    private List<String> ingredients;

    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public Order() {

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

}
