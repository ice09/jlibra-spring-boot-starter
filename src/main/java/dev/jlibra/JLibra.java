package dev.jlibra;

import dev.jlibra.admissioncontrol.AdmissionControl;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class JLibra {

    public static final String DEFAULT_SERVICE_HOST = "ac.testnet.libra.org";
    public static final int DEFAULT_SERVICE_PORT    = 8000;
    public static final String DEFAULT_FAUCET_HOST  = "faucet.testnet.libra.org";
    public static final int DEFAULT_FAUCET_PORT     = 80;
    public static final long DEFAULT_GAS_UNIT_PRICE = 0;
    public static final long DEFAULT_MAX_GAS_AMOUNT = 0;

    private final String faucetHost;
    private final int faucetPort;
    private final long gasUnitPrice;
    private final long maxGasAmount;
    private final AdmissionControl admissionControl;

    public JLibra() {
        this(DEFAULT_SERVICE_HOST, DEFAULT_SERVICE_PORT, DEFAULT_FAUCET_HOST, DEFAULT_FAUCET_PORT, DEFAULT_GAS_UNIT_PRICE, DEFAULT_MAX_GAS_AMOUNT);
    }

    public JLibra(String host, int port) {
        this(host, port, DEFAULT_FAUCET_HOST, DEFAULT_FAUCET_PORT, DEFAULT_GAS_UNIT_PRICE, DEFAULT_MAX_GAS_AMOUNT);
    }

    public JLibra(String host, int port, String faucetHost, int faucetPort) {
        this(host, port, faucetHost, faucetPort, DEFAULT_GAS_UNIT_PRICE, DEFAULT_MAX_GAS_AMOUNT);
    }

    public JLibra(String host, int port, long gasUnitPrice, long maxGasAmount) {
        this(host, port, DEFAULT_FAUCET_HOST, DEFAULT_FAUCET_PORT, gasUnitPrice, maxGasAmount);
    }

    public JLibra(String host, int port, String faucetHost, int faucetPort, long gasUnitPrice, long maxGasAmount) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.admissionControl = new AdmissionControl(channel);
        this.faucetHost = faucetHost;
        this.faucetPort = faucetPort;
        this.gasUnitPrice = gasUnitPrice;
        this.maxGasAmount = maxGasAmount;
    }

    public String getFaucetHost() {
        return faucetHost;
    }

    public int getFaucetPort() {
        return faucetPort;
    }

    public long getGasUnitPrice() {
        return gasUnitPrice;
    }

    public long getMaxGasAmount() {
        return maxGasAmount;
    }

    public AdmissionControl getAdmissionControl() {
        return admissionControl;
    }

}
