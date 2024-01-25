package project.smartcontactmanager.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class MyOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long myOrderId;

    private String orderId;
    private Integer amount;
    private String receipt;
    private String status;

    @ManyToOne
    private User user;

    private String paymentId;

    public MyOrders() {
    }

    public long getMyOrderId() {
        return myOrderId;
    }

    public void setMyOrderId(long myOrderId) {
        this.myOrderId = myOrderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    

}
