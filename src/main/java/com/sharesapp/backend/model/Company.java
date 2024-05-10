package com.sharesapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "companies")
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

    @Column(name = "shares")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "share_id")
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
