package com.bank.service.internal;

import com.bank.domain.InsufficientFundsException;
import com.bank.domain.InvalidTimePolicy;
import com.bank.domain.Time;
import com.bank.domain.TransferReceipt;
import com.bank.repository.AccountNotFoundException;
import com.bank.repository.AccountRepository;
import com.bank.repository.internal.SimpleAccountRepository;
import com.bank.service.FeePolicy;
import com.bank.service.TransferService;
import org.junit.Before;
import org.junit.Test;

import static com.bank.repository.internal.SimpleAccountRepository.Data.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DefaultTransferServiceTests {

    private AccountRepository accountRepository;
    private TransferService transferService;
    private TimePolicy timePolicy;

    @Before
    public void setUp() {
        accountRepository = new SimpleAccountRepository();
        timePolicy = new TimePolicy(new Time("19:00", "06:00", "22:00"));
        FeePolicy feePolicy = new ZeroFeePolicy();
        transferService = new DefaultTransferService(accountRepository, feePolicy, timePolicy);

        assertThat(accountRepository.findById(A123_ID).getBalance(), equalTo(A123_INITIAL_BAL));
        assertThat(accountRepository.findById(C456_ID).getBalance(), equalTo(C456_INITIAL_BAL));
    }

    @Test
    public void testTransfer() throws InsufficientFundsException {
        double transferAmount = 100.00;

        TransferReceipt receipt = transferService.transfer(transferAmount, A123_ID, C456_ID);

        assertThat(receipt.getTransferAmount(), equalTo(transferAmount));
        assertThat(receipt.getFinalSourceAccount().getBalance(), equalTo(A123_INITIAL_BAL - transferAmount));
        assertThat(receipt.getFinalDestinationAccount().getBalance(), equalTo(C456_INITIAL_BAL + transferAmount));

        assertThat(accountRepository.findById(A123_ID).getBalance(), equalTo(A123_INITIAL_BAL - transferAmount));
        assertThat(accountRepository.findById(C456_ID).getBalance(), equalTo(C456_INITIAL_BAL + transferAmount));
    }

    @Test
    public void testInsufficientFunds() {
        double overage = 9.00;
        double transferAmount = A123_INITIAL_BAL + overage;

        try {
            transferService.transfer(transferAmount, A123_ID, C456_ID);
            fail("expected InsufficientFundsException");
        } catch (InsufficientFundsException ex) {
            assertThat(ex.getTargetAccountId(), equalTo(A123_ID));
            assertThat(ex.getOverage(), equalTo(overage));
        }

        assertThat(accountRepository.findById(A123_ID).getBalance(), equalTo(A123_INITIAL_BAL));
        assertThat(accountRepository.findById(C456_ID).getBalance(), equalTo(C456_INITIAL_BAL));
    }

    @Test
    public void testNonExistentSourceAccount() throws InsufficientFundsException {
        try {
            transferService.transfer(1.00, Z999_ID, C456_ID);
            fail("expected AccountNotFoundException");
        } catch (AccountNotFoundException ex) {
        }

        assertThat(accountRepository.findById(C456_ID).getBalance(), equalTo(C456_INITIAL_BAL));
    }

    @Test
    public void testNonExistentDestinationAccount() throws InsufficientFundsException {
        try {
            transferService.transfer(1.00, A123_ID, Z999_ID);
            fail("expected AccountNotFoundException");
        } catch (AccountNotFoundException ex) {
        }

        assertThat(accountRepository.findById(A123_ID).getBalance(), equalTo(A123_INITIAL_BAL));
    }

    @Test
    public void testZeroTransferAmount() throws InsufficientFundsException {
        try {
            transferService.transfer(0.00, A123_ID, C456_ID);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testNegativeTransferAmount() throws InsufficientFundsException {
        try {
            transferService.transfer(-100.00, A123_ID, C456_ID);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testTransferAmountLessThanOneCent() throws InsufficientFundsException {
        try {
            transferService.transfer(0.009, A123_ID, C456_ID);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testCustomizedMinimumTransferAmount() throws InsufficientFundsException {
        transferService.transfer(1.00, A123_ID, C456_ID); // should be fine
        transferService.setMinimumTransferAmount(10.00);
        transferService.transfer(10.00, A123_ID, C456_ID); // fine against new minimum
        try {
            transferService.transfer(9.00, A123_ID, C456_ID); // violates new minimum!
            fail("expected IllegalArgumentException on 9.00 transfer that violates 10.00 minimum");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testNonZeroFeePolicy() throws InsufficientFundsException {
        double flatFee = 5.00;
        double transferAmount = 10.00;
        transferService = new DefaultTransferService(accountRepository, new FlatFeePolicy(flatFee), timePolicy);
        transferService.transfer(transferAmount, A123_ID, C456_ID);
        assertThat(accountRepository.findById(A123_ID).getBalance(), equalTo(A123_INITIAL_BAL - transferAmount - flatFee));
        assertThat(accountRepository.findById(C456_ID).getBalance(), equalTo(C456_INITIAL_BAL + transferAmount));
    }

    @Test(expected = InvalidTimePolicy.class)
    public void transfer_invalid_policy_should_see_exception() throws InsufficientFundsException, InvalidTimePolicy{
        double transferAmount = 100.00;
        FeePolicy feePolicy = new ZeroFeePolicy();
        TimePolicy timePolicy = new TimePolicy(new Time("23:00", "06:00", "22:00"));
        DefaultTransferService transferService = new DefaultTransferService(accountRepository, feePolicy, timePolicy);
        TransferReceipt transfer = transferService.transfer(transferAmount, A123_ID, C456_ID);
    }

}
