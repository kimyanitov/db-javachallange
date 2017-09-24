package com.db.awmd.challenge.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicStampedReference;

@Value
public class AccountEntity {

  private final String accountId;

  private final AtomicStampedReference<BigDecimal> balance;

  public AccountEntity(String accountId, BigDecimal balance, int counter) {
    this.accountId = accountId;
    this.balance = new AtomicStampedReference<>(balance, counter);
  }

}
