package dev.jlibra.spring.actuate;

import dev.jlibra.JLibra;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.util.Assert;

/**
 * Health check indicator for JLibra
 */
public class JLibraHealthIndicator extends AbstractHealthIndicator {

    private JLibra jlibra;

    public JLibraHealthIndicator(JLibra jlibra) {
        Assert.notNull(jlibra, "JLibra must not be null");
        this.jlibra = jlibra;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        if (jlibra.getAdmissionControl() == null) {
            builder.down();
        } else {
            builder.up();
            builder.withDetail("admission-control", jlibra.getAdmissionControl().toString());
        }
    }
}
