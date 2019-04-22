package za.co.atpsa.atp.entities;

public class Collections {
    private String date;
    private String description;
    private  int processed;
    private double amount;
    private int number_of_payments;
    private String name;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getNumber_of_payments() {
        return number_of_payments;
    }

    public void setNumber_of_payments(int number_of_payments) {
        this.number_of_payments = number_of_payments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

