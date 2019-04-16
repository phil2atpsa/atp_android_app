package za.co.atpsa.atp.entities;

import java.io.Serializable;

public class ProductBalance implements Serializable {

    private String product_name;
    private String icon;
    private String description;
    private  int balance;
    private double credit_price;
    private int product_id;

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public double getCredit_price() {
        return credit_price;
    }

    public void setCredit_price(double credit_price) {
        this.credit_price = credit_price;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
}
