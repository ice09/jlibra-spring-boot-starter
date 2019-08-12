package dev.jlibra.spring.action;

import admission_control.AdmissionControlOuterClass.AdmissionControlStatus;
import com.google.protobuf.ByteString;
import dev.jlibra.JLibra;
import dev.jlibra.KeyUtils;
import dev.jlibra.admissioncontrol.query.AccountState;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.*;
import dev.jlibra.move.Move;
import mempool.MempoolStatus.MempoolAddTransactionStatus;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import static java.time.Instant.now;
import static java.util.Arrays.asList;

@Component
public class PeerToPeerTransfer {

    @Autowired
    private JLibra jLibra;

    public PeerToPeerTransferReceipt transferFunds(String toAddress, long amountInMicroLibras, PublicKey publicKey,
                                                   PrivateKey privateKey, long gasUnitPrice, long maxGasAmount) {
        U64Argument amountArgument = new U64Argument(amountInMicroLibras);
        AddressArgument addressArgument = new AddressArgument(Hex.decode(toAddress));

        long validMaxGasAmount = (maxGasAmount == -1) ? jLibra.getMaxGasAmount() : maxGasAmount;
        long validGasUnitPrice = (gasUnitPrice == -1) ? jLibra.getGasUnitPrice() : gasUnitPrice;

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(fetchLatestSequenceNumber(KeyUtils.toByteArrayLibraAddress(publicKey.getEncoded())))
                .maxGasAmount(validMaxGasAmount)
                .gasUnitPrice(validGasUnitPrice)
                .expirationTime(now().getEpochSecond() + 10000L)
                .program(ImmutableProgram.builder().code(ByteString.copyFrom(Move.peerToPeerTransferAsBytes())).addAllArguments(asList(addressArgument, amountArgument)).build()).build();

        SubmitTransactionResult result = jLibra.getAdmissionControl().submitTransaction(publicKey, privateKey,
                transaction);
        return new PeerToPeerTransferReceipt(result);
    }

    protected long fetchLatestSequenceNumber(byte[] address) {
        UpdateToLatestLedgerResult result = jLibra.getAdmissionControl()
                .updateToLatestLedger(ImmutableQuery.builder().addAccountStateQueries(ImmutableGetAccountState.builder().address(address).build()).build());

        return result.getAccountStates()
                .stream()
                .filter(accountState -> Arrays.equals(
                        accountState.getAddress(),
                        address))
                .map(AccountState::getSequenceNumber)
                .findFirst()
                .orElse(0L);
    }

    public static class PeerToPeerTransferReceipt {

        public enum Status {
            OK, FAIL
        }

        private Status status;

        private PeerToPeerTransferReceipt(SubmitTransactionResult result) {
            if (result.getAdmissionControlStatus() == AdmissionControlStatus.Accepted
                    && result.getMempoolStatus() == MempoolAddTransactionStatus.Valid) {
                this.status = Status.OK;
            } else {
                this.status = Status.FAIL;
            }
        }

        public Status getStatus() {
            return status;
        }

    }
}