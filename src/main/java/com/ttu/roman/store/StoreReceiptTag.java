package com.ttu.roman.store;


import javax.persistence.*;

@Entity
@Table(name = "store_receipt_tag")
public class StoreReceiptTag {
    @Id
    @SequenceGenerator(name="store_receipt_tag_id", initialValue=1, allocationSize=1, catalog = "financemanager",
            schema = "public", sequenceName = "store_receipt_tag_id")
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "store_receipt_tag_id")
    @Column(name = "store_receipt_tag_id")
    private Long id;

    @Column(name = "tag")
    private String tag;

    @Column(name = "status")
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tagName) {
        this.tag = tag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
