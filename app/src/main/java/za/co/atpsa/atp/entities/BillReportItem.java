package za.co.atpsa.atp.entities;

import java.io.Serializable;

public class BillReportItem  implements Serializable {

    private String ref;
    private String name;
    private String email;
    private String cell_no;
    private String description;
    private double amount;
    private String status;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCell_no() {
        return cell_no;
    }

    public void setCell_no(String cell_no) {
        this.cell_no = cell_no;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
