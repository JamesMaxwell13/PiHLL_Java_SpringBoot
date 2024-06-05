package com.sharesapp.backend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "shares")
@AllArgsConstructor
@NoArgsConstructor
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

  @ManyToMany(mappedBy = "shares")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<User> users = new HashSet<>();

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "company_id", nullable = false)
  private Company company = new Company();
}
