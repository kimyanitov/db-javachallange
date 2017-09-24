package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

//@Value
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
@Accessors(fluent = true)
@NoArgsConstructor
public class Transfer {

    @NotNull
    @NotEmpty
    String accountFromId;

    @NotNull
    @NotEmpty
    String accountToId;

    @NotNull
    @Min(value = 0, message = "Initial balance must be positive.")
    BigDecimal amount;

//    @JsonCreator
//    public Account(@JsonProperty("accountId") String accountId,
//                   @JsonProperty("balance") BigDecimal balance) {
//        this.accountId = accountId;
//        this.balance = balance;
//    }
}


