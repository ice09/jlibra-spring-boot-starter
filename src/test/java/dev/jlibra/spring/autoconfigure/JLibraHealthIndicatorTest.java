package dev.jlibra.spring.autoconfigure;

import dev.jlibra.JLibra;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.spring.autoconfigure.context.SpringApplicationTest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringApplicationTest.class)
public class JLibraHealthIndicatorTest {


    @Autowired
    HealthIndicator jlibraHealthIndicator;

    @Autowired
    JLibra jlibra;

    @Test
    public void testHealthCheckIndicatorDown() throws Exception {
        mockJLibraCalls(null);
        Health health = jlibraHealthIndicator.health();
        assertThat(health.getStatus(), equalTo(Status.DOWN));
    }

    @Test
    public void testHealthCheckIndicatorUp() throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 11111)
                .usePlaintext()
                .build();
        AdmissionControl admissionControl = new AdmissionControl(channel);
        mockJLibraCalls(admissionControl);

        Health health = jlibraHealthIndicator.health();
        assertThat(health.getStatus(), equalTo(Status.UP));
        assertThat(health.getDetails().get("admission-control").toString(), endsWith("target=localhost:11111}}"));
    }

    private void mockJLibraCalls(AdmissionControl admissionControl) {
        if (admissionControl != null) {
            Mockito.when(jlibra.getAdmissionControl()).thenReturn(admissionControl);
        }
    }

}