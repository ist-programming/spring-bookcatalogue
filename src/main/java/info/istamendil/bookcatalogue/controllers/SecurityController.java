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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import info.istamendil.bookcatalogue.models.User;
import info.istamendil.bookcatalogue.models.forms.LoginForm;
import info.istamendil.bookcatalogue.repositories.UserAuthorityRepository;
import info.istamendil.bookcatalogue.services.UserService;

/**
 *
 * @author Alexander Ferenets (aka Istamendil) – http://istamendil.info
 */
@Controller
public class SecurityController {

  @Autowired
  private UserService userService;

  @Autowired
  private UserAuthorityRepository userAuthorityRepo;
  
  protected String showRegisterForm(ModelMap map){
    map.put("userAuthorities", userAuthorityRepo.findAll());
    return "security/register_form";
  }

  @RequestMapping(value = "/login")
  @PreAuthorize("isAnonymous()")
  public String login(@RequestParam(required = false) String error, @ModelAttribute("loginForm") LoginForm loginForm, BindingResult result, ModelMap map) {
    map.put("error", error);
    return "security/login_form";
  }

  @RequestMapping(value = "/register", method = RequestMethod.GET)
  @PreAuthorize("isAnonymous()")
  public String register(ModelMap map) {
    map.put("user", new User());
    return showRegisterForm(map);
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  @PreAuthorize("isAnonymous()")
  public String registerHandler(
    RedirectAttributes redirectAttributes,
    @ModelAttribute("user") @Valid User user,
    BindingResult result,
    ModelMap map
  ) {
    if (!result.hasErrors()) {
      try{
        userService.registerUser(user);
        redirectAttributes.addFlashAttribute("message", "You has been registered successfully");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:" + UriComponentsBuilder.fromPath("/").build();
      }
      catch(DuplicateKeyException ex){
        result.rejectValue("username", "entry.duplicate", "There is account with such email already.");
      }
    }
    return showRegisterForm(map);
  }

  @RequestMapping("/profile")
  @PreAuthorize("isAuthenticated()")
  public String profile(HttpServletRequest request) {
    return "security/profile";
  }
}
