package dev.jlibra.spring.action;

import admission_control.AdmissionControlOuterClass;
import com.google.protobuf.ByteString;
import dev.jlibra.JLibra;
import dev.jlibra.KeyUtils;
import dev.jlibra.LibraHelper;
import dev.jlibra.admissioncontrol.query.AccountResource;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.*;
import dev.jlibra.move.Move;
import mempool.MempoolStatus;
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
        AccountAddressArgument addressArgument = new AccountAddressArgument(Hex.decode(toAddress));

        long validMaxGasAmount = (maxGasAmount == -1) ? jLibra.getMaxGasAmount() : maxGasAmount;
        long validGasUnitPrice = (gasUnitPrice == -1) ? jLibra.getGasUnitPrice() : gasUnitPrice;

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(maybeFindSequenceNumber(KeyUtils.toHexStringLibraAddress(publicKey.getEncoded())))
                .maxGasAmount(validMaxGasAmount)
                .gasUnitPrice(validGasUnitPrice)
                .senderAccount(KeyUtils.toByteArrayLibraAddress(publicKey.getEncoded()))
                .expirationTime(now().getEpochSecond() + 10000L)
                .program(ImmutableProgram.builder().code(ByteString.copyFrom(Move.peerToPeerTransferAsBytes())).addAllArguments(asList(addressArgument, amountArgument)).build()).build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(KeyUtils.stripPublicKeyPrefix(publicKey.getEncoded()))
                .transaction(transaction)
                .signature(LibraHelper.signTransaction(transaction, privateKey))
                .build();

        SubmitTransactionResult result = jLibra.getAdmissionControl().submitTransaction(signedTransaction);
        return new PeerToPeerTransferReceipt(result);
    }

    protected long maybeFindSequenceNumber(String address) {
        UpdateToLatestLedgerResult result = jLibra.getAdmissionControl().updateToLatestLedger(
                ImmutableQuery.builder().addAccountStateQueries(
                        ImmutableGetAccountState.builder().address(Hex.decode(address)).build()).build());

        return result.getAccountStates()
                .stream()
                .filter(accountState -> Arrays.equals(
                        accountState.getAuthenticationKey(),
                        Hex.decode(address)))
                .map(AccountResource::getSequenceNumber)
                .findFirst()
                .orElse(0);
    }

    public static class PeerToPeerTransferReceipt {

        public enum Status {
            OK, FAIL
        }

        private Status status;

        private PeerToPeerTransferReceipt(SubmitTransactionResult result) {
            if (result.getAdmissionControlStatus().getCode() == AdmissionControlOuterClass.AdmissionControlStatusCode.Accepted
                    && result.getMempoolStatus().getCode() == MempoolStatus.MempoolAddTransactionStatusCode.Valid) {
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