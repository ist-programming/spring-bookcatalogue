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
package info.istamendil.bookcatalogue.config;

import javax.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import info.istamendil.bookcatalogue.models.Book;
import info.istamendil.bookcatalogue.utils.StringToEntityConverter;
import info.istamendil.bookcatalogue.models.PublishingHouse;
import info.istamendil.bookcatalogue.models.UserAuthority;
import org.springframework.context.annotation.Import;

/**
 *
 * @author Alexander Ferenets (aka Istamendil) – http://istamendil.info
 */
@Configuration
@ComponentScan("info.istamendil.bookcatalogue.controllers")
@Import({ViewResolverConfig.class})
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/assets/js/");
    registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/assets/css/");
    registry.addResourceHandler("/fonts/**").addResourceLocations("/WEB-INF/assets/fonts/");
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/403").setViewName("error/access_denied");
    registry.addViewController("/about").setViewName("static/about");
  }
  
  @Bean
  public Validator validator(){
    return new LocalValidatorFactoryBean();
  }

  @Override
  public void addFormatters(FormatterRegistry formatterRegistry) {
    formatterRegistry.addConverter(bookGenericConverter());
    formatterRegistry.addConverter(publishingHouseGenericConverter());
    formatterRegistry.addConverter(userAuthorityGenericConverter());
  }

  @Bean
  public StringToEntityConverter bookGenericConverter(){
    return new StringToEntityConverter(Book.class);
  }
  @Bean
  public StringToEntityConverter publishingHouseGenericConverter(){
    return new StringToEntityConverter(PublishingHouse.class);
  }
  @Bean
  public StringToEntityConverter userAuthorityGenericConverter(){
    return new StringToEntityConverter(UserAuthority.class);
  }
}
