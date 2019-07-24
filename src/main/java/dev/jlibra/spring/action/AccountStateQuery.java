package dev.jlibra.spring.action;

import dev.jlibra.JLibra;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountTransactionBySequenceNumber;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountStateQuery {

    @Autowired
    private JLibra jLibra;

    public UpdateToLatestLedgerResult queryBalance(byte[] address) {
        return jLibra.getAdmissionControl()
                .updateToLatestLedger(ImmutableQuery.builder()
                        .addAccountStateQueries(ImmutableGetAccountState.builder()
                                .address(address)
                                .build())
                        .build());
    }

    public UpdateToLatestLedgerResult queryTransactionsBySequenceNumber(byte[] address, long sequenceNumber) {
        return jLibra.getAdmissionControl()
                .updateToLatestLedger(ImmutableQuery.builder()
                        .addAccountTransactionBySequenceNumberQueries(ImmutableGetAccountTransactionBySequenceNumber.builder()
                                .accountAddress(address).sequenceNumber(sequenceNumber)
                                .build())
                        .build());
    }

}
