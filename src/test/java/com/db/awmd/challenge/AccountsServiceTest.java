package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.BalanceTransferException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

    @Autowired
    private AccountsService accountsService;
    @MockBean
    private NotificationService notificationService;

    @Before
    public void setUp() throws Exception {
        accountsService.getAccountsRepository().clearAccounts();
    }

    @Test
    public void addAccount() throws Exception {
        Account account = new Account("Id-123");
        account.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(account);

        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
    }

    @Test
    public void addAccount_failsOnDuplicateId() throws Exception {
        String uniqueId = "Id-" + System.currentTimeMillis();
        Account account = new Account(uniqueId);
        this.accountsService.createAccount(account);

        try {
            this.accountsService.createAccount(account);
            fail("Should have failed when adding duplicate account");
        } catch (DuplicateAccountIdException ex) {
            assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
        }
    }

    @Test
    public void shouldUpdateBothLegsWhenTransferAndNotifySides() throws Exception {
        Account from = new Account("Id-from", new BigDecimal("100.00"));
        Account to = new Account("Id-to", new BigDecimal("9.99"));
        accountsService.createAccount(from);
        accountsService.createAccount(to);

        Transfer transfer = new Transfer(from.getAccountId(), to.getAccountId(), new BigDecimal("5.50"));
        accountsService.transfer(transfer);

        BigDecimal newFromBalance = accountsService.getAccount(from.getAccountId()).getBalance();
        BigDecimal newToBalance = accountsService.getAccount(to.getAccountId()).getBalance();
        assertEquals(from.getBalance().subtract(transfer.getAmount()), newFromBalance);
        assertEquals(to.getBalance().add(transfer.getAmount()), newToBalance);

        verify(notificationService, times(1))
                .notifyAboutTransfer(new Account(from.getAccountId(), newFromBalance),
                        format("You have sent %s to %s.", transfer.getAmount(), transfer.getAccountToId()));
        verify(notificationService, times(1))
                .notifyAboutTransfer(new Account(to.getAccountId(), newToBalance),
                        format("You have received %s from %s.", transfer.getAmount(), transfer.getAccountFromId()));
    }


    @Test
    public void shouldNotTransferWhenNotEnoughBalance() throws Exception {
        Account from = new Account("Id-from", new BigDecimal("1.00"));
        Account to = new Account("Id-to", new BigDecimal("9.99"));
        accountsService.createAccount(from);
        accountsService.createAccount(to);

        Transfer transfer = new Transfer(from.getAccountId(), to.getAccountId(), new BigDecimal("5.50"));
        try {
            accountsService.transfer(transfer);
            fail("Should have failed due to insufficient balance on donor.");
        } catch (BalanceTransferException bte) {
            assertThat(bte.getMessage()).isEqualTo(format("Amount is too low for account %s to transfer %s", transfer.getAccountFromId(), transfer.getAmount()));
        }

        BigDecimal newFromBalance = accountsService.getAccount(from.getAccountId()).getBalance();
        BigDecimal newToBalance = accountsService.getAccount(to.getAccountId()).getBalance();
        assertEquals(from.getBalance(), newFromBalance);
        assertEquals(to.getBalance(), newToBalance);

        verify(notificationService, never()).notifyAboutTransfer(any(), any());
    }
}
