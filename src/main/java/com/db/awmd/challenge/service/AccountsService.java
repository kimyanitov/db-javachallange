package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AccountEntity;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.BalanceTransferException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.lang.String.format;

@Service
public class AccountsService {

    @Getter
    private final AccountsRepository accountsRepository;
    private final NotificationService notificationService;

    @Autowired
    public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
        this.accountsRepository = accountsRepository;
        this.notificationService = notificationService;
    }

    public void createAccount(Account account) {
        this.accountsRepository.createAccount(new AccountEntity(account.getAccountId(), account.getBalance(), 0));
    }

    public Account getAccount(String accountId) {
        AccountEntity account = this.accountsRepository.getAccount(accountId);
        return new Account(account.getAccountId(), account.getBalance().getReference());
    }

    public void transfer(Transfer transfer) {
        debit(transfer);
        credit(transfer);
    }

    private void debit(Transfer transfer) {
        AccountEntity from = accountsRepository.getAccount(transfer.getAccountFromId());

        boolean updated = false;
        BigDecimal newFromBalance = null;
        while (!updated) {
            int[] fromStampHolder = new int[1];
            BigDecimal fromBalance = from.getBalance().get(fromStampHolder);
            newFromBalance = fromBalance.subtract(transfer.getAmount());

            if (newFromBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new BalanceTransferException(format("Amount is too low for account %s to transfer %s", transfer.getAccountFromId(), transfer.getAmount()));
            }

            if (!from.getBalance().compareAndSet(fromBalance, newFromBalance, fromStampHolder[0], fromStampHolder[0] + 1)) {
                continue;
            }
            updated = true;
        }
        try {
            accountsRepository.update(from);
        } catch (Throwable t) {
            //todo since we can't be sure at which stage exception was thrown
            // we need to re-read the db and verify if update made it there before sending notification.
        }
        notificationService.notifyAboutTransfer(new Account(from.getAccountId(), newFromBalance), format("You have sent %s to %s.", transfer.getAmount(), transfer.getAccountToId()));
    }

    private void credit(Transfer transfer) {
        AccountEntity to = accountsRepository.getAccount(transfer.getAccountToId());
        boolean updated = false;
        BigDecimal newToBalance = null;
        while (!updated) {
            int[] toStampHolder = new int[1];
            BigDecimal toBalance = to.getBalance().get(toStampHolder);
            newToBalance = toBalance.add(transfer.getAmount());

            if (!to.getBalance().compareAndSet(toBalance, newToBalance, toStampHolder[0], toStampHolder[0] + 1)) {
                continue;
            }
            updated = true;
        }

        try {
            accountsRepository.update(to);
        } catch (Throwable t) {
            //todo since we can't be sure at which stage exception was thrown
            // we need to re-read the db and verify if update made it there before sending notification.
        }
        notificationService.notifyAboutTransfer(new Account(to.getAccountId(), newToBalance), format("You have received %s from %s.", transfer.getAmount(), transfer.getAccountFromId()));
    }
}
