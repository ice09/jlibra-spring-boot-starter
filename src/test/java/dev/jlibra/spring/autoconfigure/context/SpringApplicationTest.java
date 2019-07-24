package dev.jlibra.spring.autoconfigure.context;

import dev.jlibra.JLibra;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@SpringBootApplication
public class SpringApplicationTest {
    @Bean
    @Primary
    public JLibra nameService() {
        return mock(JLibra.class, Mockito.RETURNS_DEEP_STUBS);
    }

}
