package com.sharesapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "shares")
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prev_close_price")
    private Float prevClosePrice;

    @Column(name = "high_price")
    private Float highPrice;

    @Column(name = "low_price")
    private Float lowPrice;

    @Column(name = "open_price")
    private Float openPrice;

    @Column(name = "last_sale_price")
    private Float lastSalePrice;

    @Column(name = "last_time_update")
    private Instant lastTimeUpdated;

    @Column(name = "symbol")
    private String symbol;

    @ManyToMany
    @JoinTable(name = "shares_users", joinColumns = @JoinColumn(name = "share_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company = new Company();

    public void setCompanyId(Long companyId) {
        this.company.setId(companyId);
    }
}
