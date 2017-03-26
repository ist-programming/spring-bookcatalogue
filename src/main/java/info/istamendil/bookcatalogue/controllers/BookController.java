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

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import info.istamendil.bookcatalogue.exceptions.NotFoundException;
import info.istamendil.bookcatalogue.models.Book;
import info.istamendil.bookcatalogue.repositories.BookRepository;
import info.istamendil.bookcatalogue.repositories.PublishingHouseRepository;

/**
 *
 * @author Alexander Ferenets (aka Istamendil) – http://istamendil.info
 */
@Controller
public class BookController {

  @Autowired
  private BookRepository bookRepo;

  @Autowired
  private PublishingHouseRepository publishingHouseRepository;

  @RequestMapping("/")
  public String list(ModelMap map) {
    map.put("books", bookRepo.findAll());
    return "books/list";
  }

  @RequestMapping("/book/{id}")
  public String show(@PathVariable("id") Book book, ModelMap map) {
    if (book == null) {
      throw new NotFoundException("book");
    }
    map.put("book", book);
    return "books/show";
  }

  @RequestMapping(value = "/book/new", method = RequestMethod.GET)
  @PreAuthorize("hasRole('ADMIN')")
  public String add(ModelMap map) {
    map.put("book", new Book());
    return showForm(map);
  }

  @RequestMapping(value = "/book/new", method = RequestMethod.POST)
  @PreAuthorize("hasRole('ADMIN')")
  public String addHandler(
    RedirectAttributes redirectAttributes,
    @ModelAttribute("book") @Valid Book book,
    BindingResult result,
    ModelMap map
  ) {
    if (result.hasErrors()) {
      return showForm(map);
    } else {
      bookRepo.save(book);
      redirectAttributes.addFlashAttribute("message", "Book \"" + book.getName() + "\" has been saved successfully");
      redirectAttributes.addFlashAttribute("messageType", "success");
      return "redirect:" + MvcUriComponentsBuilder.fromMappingName("BC#add").build();
    }
  }

  @RequestMapping(value = "/book/edit/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasRole('ADMIN')")
  public String edit(@PathVariable("id") Book book, ModelMap map) {
    if (book == null) {
      throw new NotFoundException("book");
    }
    map.put("book", book);
    return showForm(map);
  }

  @RequestMapping(value = "/book/edit/{id}", method = RequestMethod.POST)
  @PreAuthorize("hasRole('ADMIN')")
  public String editHandler(
    RedirectAttributes redirectAttributes,
    @ModelAttribute("book") @Valid Book book,
    BindingResult result,
    ModelMap map
  ) {
    if (result.hasErrors()) {
      return showForm(map);
    } else {
      bookRepo.save(book);
      redirectAttributes.addFlashAttribute("message", "Book \"" + book.getName() + "\" has been saved successfully");
      redirectAttributes.addFlashAttribute("messageType", "success");
      return "redirect:" + MvcUriComponentsBuilder.fromMappingName("BC#edit").arg(0, book).build();
    }
  }

  @RequestMapping("/book/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public String delete(@PathVariable("id") Book book, RedirectAttributes redirectAttributes, ModelMap map) {
    try {
      bookRepo.delete(book);
      redirectAttributes.addFlashAttribute("message", "Book has been deleted successfully");
      redirectAttributes.addFlashAttribute("messageType", "success");
    }catch (DataAccessException e) {
      redirectAttributes.addFlashAttribute("message", "Can't delete book.");
      redirectAttributes.addFlashAttribute("messageType", "fail");
    }
    return "redirect:" + MvcUriComponentsBuilder.fromMappingName("BC#list").build();
  }

  protected String showForm(ModelMap map) {
    map.put("publishingHouses", publishingHouseRepository.findAll());
    return "books/book_form";
  }
}
