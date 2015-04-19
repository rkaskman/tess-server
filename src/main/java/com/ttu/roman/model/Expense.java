package com.ttu.roman.model;


import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;


@Entity
@Table(name = "processed_image_result", schema = "public")
public class Expense {
    public static final String STATE_INITIAL = "I";
    public static final String STATE_PROCESSED = "P";
    public static final String STATE_ACCEPTED = "A";

    @Id
    @SequenceGenerator(name = "processed_image_result_seq", sequenceName = "processed_image_result_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "processed_image_result_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "registration_id")
    private String registrationId;

    @Column(name = "company_reg_number")
    private String companyRegNumber;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "state")
    private String state;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "inserted_at")
    private Timestamp insertedAt;

    @Column(name = "currency")
    private String currency;

    @Column(name = "error")
    private String error;

    @Column(name = "message_id")
    private String messageId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getCompanyRegNumber() {
        return companyRegNumber;
    }

    public void setCompanyRegNumber(String companyRegNumber) {
        this.companyRegNumber = companyRegNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Timestamp getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Timestamp insertedAt) {
        this.insertedAt = insertedAt;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
