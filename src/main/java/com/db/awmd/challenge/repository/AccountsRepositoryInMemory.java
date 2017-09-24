package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.AccountEntity;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, AccountEntity> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(AccountEntity account) throws DuplicateAccountIdException {
    AccountEntity previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public AccountEntity getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

  @Override
  public void update(AccountEntity account) {
     //nothiing is needed to be doe for this in-memory implementation
  }

}
