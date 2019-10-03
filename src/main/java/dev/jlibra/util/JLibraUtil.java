package dev.jlibra.util;

import dev.jlibra.JLibra;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.AccountResource;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

public class JLibraUtil {

    private JLibra jlibra;

    private JLibraUtil() {
        // no instantiation
    }

    public JLibraUtil(JLibra jlibra) {
        this.jlibra = jlibra;
    }

    public long findBalance(String forAddress) {
        UpdateToLatestLedgerResult result = jlibra.getAdmissionControl().updateToLatestLedger(
                ImmutableQuery.builder().addAccountStateQueries(
                        ImmutableGetAccountState.builder().address(Hex.decode(forAddress)).build()).build());

        long balance = result.getAccountStates()
                .stream()
                .filter(accountState -> Arrays.equals(
                        accountState.getAuthenticationKey(),
                        Hex.decode(forAddress)))
                .map(AccountResource::getBalanceInMicroLibras)
                .findFirst()
                .orElse(0L);

        return balance;
    }

    public long maybeFindSequenceNumber(String forAddress) {
        UpdateToLatestLedgerResult result = jlibra.getAdmissionControl().updateToLatestLedger(
                ImmutableQuery.builder().addAccountStateQueries(
                        ImmutableGetAccountState.builder().address(Hex.decode(forAddress)).build()).build());

        return result.getAccountStates()
                .stream()
                .filter(accountState -> Arrays.equals(
                        accountState.getAuthenticationKey(),
                        Hex.decode(forAddress)))
                .map(AccountResource::getSequenceNumber)
                .findFirst()
                .orElse(0);
    }

    public long mint(String address, long amount) {
        long amountInMicroLibras = 1_000_000_000;

        HttpResponse<String> response = Unirest.post(jlibra.getFaucetHost() + ":" + jlibra.getFaucetPort())
                .queryString("amount", amountInMicroLibras)
                .queryString("address", address)
                .asString();

        if (response.getStatus() != 200) {
            throw new IllegalStateException(String.format("Minting %d for %s at faucet %s:%d failed.", amount, address, jlibra.getFaucetHost(), jlibra.getFaucetPort()));
        }

        return amountInMicroLibras;
    }

}
