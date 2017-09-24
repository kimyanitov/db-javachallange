package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
@NoArgsConstructor
public class Transfer {

    @NotNull
    @NotEmpty
    private String accountFromId;

    @NotNull
    @NotEmpty
    private String accountToId;

    @NotNull
    @Min(value = 0, message = "Initial balance must be positive.")
    private BigDecimal amount;

}


