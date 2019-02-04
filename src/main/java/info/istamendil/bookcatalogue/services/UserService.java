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
package info.istamendil.bookcatalogue.services;

import java.util.Set;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import info.istamendil.bookcatalogue.models.User;
import info.istamendil.bookcatalogue.models.UserAuthority;
import info.istamendil.bookcatalogue.repositories.UserAuthorityRepository;
import info.istamendil.bookcatalogue.repositories.UserRepository;
import java.util.NoSuchElementException;

/**
 *
 * @author Alexander Ferenets (aka Istamendil) – http://istamendil.info
 */
@Service
public class UserService implements UserDetailsService {

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private UserAuthorityRepository userAuthorityRepo;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    return userRepo.findByUsername(username);
  }

  public void registerUser(User user) {
    if(userRepo.findByUsername(user.getUsername()) != null){
      throw new DuplicateKeyException("Duplicate key - username field.");
    }
    user.addAuthority(userAuthorityRepo.findByAuthority("ROLE_USER"));
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setPasswordRepeat(user.getPassword());
    userRepo.save(user);
  }
  
  public User updateFullNameAndAuthorities(Integer id, String fullName, Set<UserAuthority> authorities) {
    try {
      User user = userRepo.findById(id).get();
      user.setPasswordRepeat(user.getPassword());
      user.setFullName(fullName);
      user.setAuthorities(authorities);
      return user;
    } catch (NoSuchElementException ex) {
      throw new EntityNotFoundException("User with id " + id + "has not been found.");
    }
  }

}
