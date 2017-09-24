package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.AccountEntity;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

public interface AccountsRepository {

  void createAccount(AccountEntity account) throws DuplicateAccountIdException;

  AccountEntity getAccount(String accountId);

  void clearAccounts();

  void update(AccountEntity account);
}
