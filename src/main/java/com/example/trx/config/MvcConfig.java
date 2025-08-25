package com.example.trx.config;

import com.example.trx.support.interceptor.LoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
public class MvcConfig  implements WebMvcConfigurer {

    private LoggingInterceptor loggingInterceptor;

    public MvcConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET","POST")
                .allowedHeaders("*");
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger-ui/**","/swagger-resources/**","/v2/api-docs");
    }
}
