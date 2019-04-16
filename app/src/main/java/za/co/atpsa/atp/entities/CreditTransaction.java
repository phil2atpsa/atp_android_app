package za.co.atpsa.atp.entities;

import java.io.Serializable;

public class CreditTransaction implements Serializable {

    private String payment_date;
    private String request_date;
    private String ref;
    private  double  amount;
    private  String   status;
    private  String   outcome;
    private  String   currency;

    public String getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(String payment_date) {
        this.payment_date = payment_date;
    }

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
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

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
