package com.staynest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig registers a "resource handler" that maps the URL pattern
 * "/uploads/**" to the PHYSICAL folder on disk where FileStorageUtil
 * saves files. Without this, images would save successfully to disk
 * but the browser would get a 404 when trying to actually display
 * them, because Spring wouldn't know that URL corresponds to a real
 * file location outside the packaged jar.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
