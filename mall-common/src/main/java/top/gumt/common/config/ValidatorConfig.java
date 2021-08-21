package top.gumt.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.gumt.common.utils.ValidatorUtils;

import javax.validation.Validator;

@Configuration
public class ValidatorConfig {

    @Bean
    public Validator validator() {
        return ValidatorUtils.getValidator();
    }
}
