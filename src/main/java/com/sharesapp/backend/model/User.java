package com.sharesapp.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email")
  private String email;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "password")
  private String password;

  @ManyToMany(mappedBy = "users")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<Share> shares = new HashSet<>();

  public User(Long id, String firstName, String lastName, String email) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  public void addShare(Share share) {
    shares.add(share);
    share.getUsers().add(this);
  }

  public void removeShare(Long shareId) {
    Share share =
        this.shares.stream().filter(t -> t.getId().equals(shareId)).findFirst().orElse(null);
    if (share != null) {
      this.shares.remove(share);
      share.getUsers().remove(this);
    }
  }
}
