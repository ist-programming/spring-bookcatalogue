/*
 * Copyright (c) 2017, Alexander Ferenets (Istamendil, ist.kazan@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the copyright holder nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package info.istamendil.bookcatalogue.models;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;
import info.istamendil.bookcatalogue.utils.FieldMatch;

/**
 *
 * @author Alexander Ferenets (aka Istamendil) – http://istamendil.info
 */
@FieldMatch.List({
  @FieldMatch(first = "password", second = "passwordRepeat", message = "The password fields must match")
})
@DynamicUpdate
@Entity
@Table(name = "user")
public class User implements CredentialsContainer, UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO) /* http://www.objectdb.com/java/jpa/entity/generated */
  private Integer id;

  @Length(max = 255)
  @NotBlank
  @Column(nullable = false)
  private String fullName;

  @NotBlank
  @Column(nullable = false)
  private String password;

  @NotBlank
  @Transient
  private String passwordRepeat;

  @ManyToMany(fetch = FetchType.EAGER, cascade={CascadeType.MERGE})
  @JoinTable(
    name = "user_user_role",
    joinColumns = @JoinColumn(name = "user"),
    inverseJoinColumns = @JoinColumn(name = "user_role")
  )
  private Set<UserAuthority> authorities = new HashSet<>();

  @Email
  @NotBlank
  @Column(nullable = false, unique = true)
  private String username;

  /*
   SPECIAL METHODS
   */
  /**
   * Clear all sensitive data.
   * http://docs.spring.io/autorepo/docs/spring-security/current/apidocs/org/springframework/security/core/CredentialsContainer.html
   */
  @Override
  public void eraseCredentials() {
    this.password = null;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 19 * hash + Objects.hashCode(this.id);
    hash = 19 * hash + Objects.hashCode(this.username);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final User other = (User) obj;
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (!Objects.equals(this.username, other.username)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return this.fullName;
  }

  /*
   GETTERS AND SETTERS
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasswordRepeat() {
    return passwordRepeat;
  }

  public void setPasswordRepeat(String passwordRepeat) {
    this.passwordRepeat = passwordRepeat;
  }

  public void addAuthority(UserAuthority authority) {
    this.authorities.add(authority);
  }

  @Override
  public Set<UserAuthority> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<UserAuthority> authorities) {
    this.authorities = authorities;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  /*
   Below there are some dummy methods. But they can be used properly with adding new fields to Entity.
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

}
