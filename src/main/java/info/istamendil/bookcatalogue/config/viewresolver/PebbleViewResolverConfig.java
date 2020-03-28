package info.istamendil.bookcatalogue.config.viewresolver;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.spring.extension.SpringExtension;
import com.mitchellbosecke.pebble.spring.servlet.PebbleViewResolver;
import info.istamendil.bookcatalogue.extensions.pebble.BaseExtension;
import info.istamendil.bookcatalogue.extensions.pebble.FullUriFunction;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.ViewResolver;

import javax.servlet.ServletContext;

@Configuration
@Profile("twig")
@ComponentScan("info.istamendil.bookcatalogue.extensions.pebble")
public class PebbleViewResolverConfig {

    @Autowired
    private ServletContext servletContext;

    @Bean
    public PebbleViewResolver viewResolver() {
        PebbleViewResolver viewResolver = new PebbleViewResolver();
        viewResolver.setPrefix("/WEB-INF/twig/");
        viewResolver.setSuffix(".twig");
        viewResolver.setPebbleEngine(pebbleEngine());
        return viewResolver;
    }

    @Bean
    public PebbleEngine pebbleEngine(){
        return new PebbleEngine.Builder().loader(pebbleTemplateLoader()).extension(pebbleExtension()).build();
    }

    @Bean
    public Loader<?> pebbleTemplateLoader(){
        return new ServletLoader(servletContext);
    }

    @Bean
    public SpringExtension pebbleExtension(){
        return new BaseExtension();
    }
}
