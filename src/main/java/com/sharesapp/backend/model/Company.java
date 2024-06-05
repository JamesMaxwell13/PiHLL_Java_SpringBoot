package com.sharesapp.backend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "companies")
@AllArgsConstructor
@NoArgsConstructor
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "capitalize")
  private Double capitalize;

  @Column(name = "adress")
  private String adress;

  @Column(name = "website")
  private String website;

  @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<Share> shares = new HashSet<>();

  public void addShare(Share share) {
    shares.add(share);
    share.setCompany(this);
  }

  public void removeShare(Long shareId) {
    Share share =
        this.shares.stream().filter(t -> t.getId().equals(shareId)).findFirst().orElse(null);
    if (share != null) {
      this.shares.remove(share);
      share.setCompany(null);
    }
  }
}
