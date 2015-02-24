package com.ttu.roman.store;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "store")
public class Store {
    public static final String STATUS_ACTIVE = "A";
    public static final String STATUS_CANCELLED = "C";

    @Id
    @SequenceGenerator(name="store_id",  sequenceName = "store_id")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "store_id")
    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "total_sum_tag")
    private String totalSumTag;

    @Column(name = "status")
    private String status;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="store_id", referencedColumnName = "store_id")
    @Where(clause = "status = 'A'")
    private List<StoreReceiptTag> storeReceiptTags;

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<StoreReceiptTag> getStoreReceiptTags() {
        return storeReceiptTags;
    }

    public void setStoreReceiptTags(List<StoreReceiptTag> storeReceiptTags) {
        this.storeReceiptTags = storeReceiptTags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalSumTag() {
        return totalSumTag;
    }

    public void setTotalSumTag(String totalSumTag) {
        this.totalSumTag = totalSumTag;
    }
}
