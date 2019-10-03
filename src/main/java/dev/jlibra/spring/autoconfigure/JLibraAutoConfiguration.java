package dev.jlibra.spring.autoconfigure;

import dev.jlibra.JLibra;
import dev.jlibra.spring.actuate.JLibraHealthIndicator;
import dev.jlibra.util.JLibraUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * jlibra auto configuration for Spring Boot.
 */
@Configuration
@ConditionalOnClass(JLibra.class)
@EnableConfigurationProperties(JLibraProperties.class)
public class JLibraAutoConfiguration {

    private static Log log = LogFactory.getLog(JLibraAutoConfiguration.class);

    @Autowired
    private JLibraProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public JLibra jlibra() {
        JLibra jlibra;
        if (!StringUtils.isEmpty(properties.getServiceUrl()) && !StringUtils.isEmpty(properties.getServicePort())) {
            if (!StringUtils.isEmpty(properties.getFaucetUrl()) && !StringUtils.isEmpty(properties.getFaucetPort())) {
                if (!StringUtils.isEmpty(properties.getGasUnitPrice()) && !StringUtils.isEmpty(properties.getMaxGasAmount())) {
                    jlibra = new JLibra(properties.getServiceUrl(), properties.getServicePort(), properties.getFaucetUrl(), properties.getFaucetPort(), properties.getGasUnitPrice(), properties.getMaxGasAmount());
                } else {
                    jlibra = new JLibra(properties.getServiceUrl(), properties.getServicePort(), properties.getFaucetUrl(), properties.getFaucetPort());
                }
            } else {
                jlibra = new JLibra(properties.getServiceUrl(), properties.getServicePort());
            }
        } else {
            jlibra = new JLibra();
        }
        log.info("Building service for endpoint: " + properties.getServiceUrl());
        return jlibra;
    }

    @Bean
    @ConditionalOnBean(JLibra.class)
    JLibraHealthIndicator jlibraHealthIndicator(JLibra jlibra) {
        return new JLibraHealthIndicator(jlibra);
    }

    @Bean
    @ConditionalOnBean(JLibra.class)
    JLibraUtil jlibraUtil(JLibra jlibra) {
        return new JLibraUtil(jlibra);
    }
}
