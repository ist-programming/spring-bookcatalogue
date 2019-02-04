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
package info.istamendil.bookcatalogue.controllers;

import java.util.Set;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import info.istamendil.bookcatalogue.exceptions.NotFoundException;
import info.istamendil.bookcatalogue.models.User;
import info.istamendil.bookcatalogue.repositories.UserAuthorityRepository;
import info.istamendil.bookcatalogue.repositories.UserRepository;
import info.istamendil.bookcatalogue.services.UserService;
import java.util.NoSuchElementException;

/**
 *
 * @author Alexander Ferenets (aka Istamendil) – http://istamendil.info
 */
@Controller
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserAuthorityRepository userAuthorityRepo;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private UserService userService;
  
  @Autowired
  private Validator validator;
  
  protected String showForm(ModelMap map){
    map.put("authorities", userAuthorityRepo.findAll());
    return "user/form";
  }
  
  @RequestMapping("/list")
  @PreAuthorize("hasRole('ADMIN')")
  public String list(ModelMap map) {
    map.put("users", userRepo.findAll());
    return "user/list";
  }

  @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasRole('ADMIN')")
  public String edit(@PathVariable int id, ModelMap map) {
    try{
      User user = userRepo.findById(id).get();
      map.put("user", user);
      return showForm(map);
    }
    catch(NoSuchElementException ex){
      throw new NotFoundException("user");
    }
  }

  @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
  @PreAuthorize("hasRole('ADMIN')")
  public String editHandler(
    RedirectAttributes redirectAttributes,
    @PathVariable int id,
    @ModelAttribute("user") User data,
    ModelMap map
  ) {
    User user;
    try{
      user = userService.updateFullNameAndAuthorities(id, data.getFullName(), data.getAuthorities());
    }
    catch(EntityNotFoundException ex){
      throw new NotFoundException("user");
    }
    Set<ConstraintViolation<User>> cv = validator.validate(user, Default.class);
    if (cv.isEmpty()){
      userRepo.save(user);
      redirectAttributes.addFlashAttribute("message", "User \"" + user.getUsername() + "\" has been saved successfully");
      redirectAttributes.addFlashAttribute("messageType", "success");
      return "redirect:" + MvcUriComponentsBuilder.fromMappingName("UC#edit").arg(0, user.getId()).build();
    }
    return showForm(map);
  }
}
