package dev.jlibra.spring.autoconfigure;

import dev.jlibra.JLibra;
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.assertThat;

public class JLibraAutoConfigurationTest {

    private AnnotationConfigApplicationContext context;

    @After
    public void tearDown() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    public void testServiceUrl() throws Exception {
        verifyServiceUrl("localhost");
    }

    @Test
    public void testServicePort() throws Exception {
        verifyServicePort(23);
    }

    private void verifyServiceUrl(String serviceUrl) throws Exception {
        load(EmptyConfiguration.class, "jlibra.service-url=" + serviceUrl, "jlibra.service-port=" + 0);
        JLibra jLibra = this.context.getBean(JLibra.class);
        assertThat(jLibra.getAdmissionControl().toString(), endsWith("target="+ serviceUrl + ":0}}"));
    }

    private void verifyServicePort(int servicePort) throws Exception {
        load(EmptyConfiguration.class, "jlibra.service-url=" + null, "jlibra.service-port=" + servicePort);
        JLibra jLibra = this.context.getBean(JLibra.class);
        assertThat(jLibra.getAdmissionControl().toString(), endsWith("target=null:" + servicePort + "}}"));
    }

    @Configuration
    static class EmptyConfiguration {
    }

    private void load(Class<?> config, String... environment) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        TestPropertyValues.of(environment).applyTo(applicationContext);
        applicationContext.register(config);
        applicationContext.register(JLibraAutoConfiguration.class);
        applicationContext.refresh();
        this.context = applicationContext;
    }

}
