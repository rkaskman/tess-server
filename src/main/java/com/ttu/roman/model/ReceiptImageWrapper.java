package com.ttu.roman.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;


@Entity
@Table(name = "receipt_picture_data")
public class ReceiptImageWrapper {
    public static final String STATE_PROCESSED = "P";
    public static final String STATE_NON_PROCESSED = "N";

    @Id
    @SequenceGenerator(name = "receipt_picture_data_seq", sequenceName = "receipt_picture_data_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "receipt_picture_data_seq")
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "registration_id")
    private String registrationId;
    @Column(name = "regnumber_picture")
    @Lob
    private String regNumberPicture;
    @Column(name = "reg_number_picture_extension")
    private String regNumberPictureExtension;
    @Column(name = "total_cost_picture")
    @Lob
    private String totalCostPicture;
    @Column(name = "total_cost_picture_extension")
    private String totalCostPictureExtension;
    @Column(name = "inserted_at")
    private Timestamp insertedAt;
    @Column(name = "state")
    private String state;

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

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegNumberPicture() {
        return regNumberPicture;
    }

    public void setRegNumberPicture(String regNumberPicture) {
        this.regNumberPicture = regNumberPicture;
    }

    public String getRegNumberPictureExtension() {
        return regNumberPictureExtension;
    }

    public void setRegNumberPictureExtension(String regNumberPictureExtension) {
        this.regNumberPictureExtension = regNumberPictureExtension;
    }

    public String getTotalCostPicture() {
        return totalCostPicture;
    }

    public void setTotalCostPicture(String totalCostPicture) {
        this.totalCostPicture = totalCostPicture;
    }

    public String getTotalCostPictureExtension() {
        return totalCostPictureExtension;
    }

    public void setTotalCostPictureExtension(String totalCostPictureExtension) {
        this.totalCostPictureExtension = totalCostPictureExtension;
    }

    public Timestamp getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Timestamp insertedAt) {
        this.insertedAt = insertedAt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}