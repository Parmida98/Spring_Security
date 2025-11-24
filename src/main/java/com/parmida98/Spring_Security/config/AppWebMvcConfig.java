package com.parmida98.Spring_Security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc   // Used in Combination with @Config & WebMvcConfigure / Spring använder dina egna WebMvc-inställningar (baserat på WebMvcConfigurer). Man använder detta när man vill styra routing, resurser, view controllers m.m. manuellt.
public class AppWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) { // skapa enkla view controllers utan att behöva skriva en riktig Controller-klass. Perfekt för statiska sidor eller enkla HTML-resurser.
        registry
                .addViewController("/")  // Path / Säger: När någon navigerar till URL / …
                .setViewName("homepage");              // Resource / så ska Spring returnera en view med namnet homepage.
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {   // kan konfigurera hur Spring ska servera statiska filer som bilder, CSS, JavaScript, ikoner osv.
        registry.addResourceHandler("/static/**")           // Alla requests som börjar med /static/ ska tolkas som statiska resurser.
                .addResourceLocations("classpath:/static");             // Path to Directory
    }
}
