package dev.jlibra.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static dev.jlibra.spring.autoconfigure.JLibraProperties.JLIBRA_PREFIX;

/**
 * jlibra property container.
 */
@ConfigurationProperties(prefix = JLIBRA_PREFIX)
public class JLibraProperties {

    public static final String JLIBRA_PREFIX = "jlibra";

    private String serviceUrl;
    private int servicePort;
    private long maxGasAmount;
    private long gasUnitPrice;
    private String faucetUrl;
    private int faucetPort;

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public long getMaxGasAmount() {
        return maxGasAmount;
    }

    public void setMaxGasAmount(long maxGasAmount) {
        this.maxGasAmount = maxGasAmount;
    }

    public long getGasUnitPrice() {
        return gasUnitPrice;
    }

    public void setGasUnitPrice(long gasUnitPrice) {
        this.gasUnitPrice = gasUnitPrice;
    }


    public String getFaucetUrl() {
        return faucetUrl;
    }

    public void setFaucetUrl(String faucetUrl) {
        this.faucetUrl = faucetUrl;
    }

    public int getFaucetPort() {
        return faucetPort;
    }

    public void setFaucetPort(int faucetPort) {
        this.faucetPort = faucetPort;
    }
}
