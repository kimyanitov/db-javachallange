package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.BalanceTransferException;
import com.db.awmd.challenge.service.AccountsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerMockTest {

    private MockMvc mockMvc;

    @MockBean
    private AccountsService accountsService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void prepareMockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void shouldAcceptTransferRequests() throws Exception {
        Transfer transfer = new Transfer("Id-1", "Id-2", new BigDecimal("12.34"));

        this.mockMvc.perform(
                post("/v1/accounts/transfer/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isCreated());

        verify(accountsService, times(1)).transfer(transfer);
    }

    @Test
    public void shouldAlwaysHavePositiveBalanceForTransfer() throws Exception {
        Transfer transfer = new Transfer("Id-1", "Id-2", new BigDecimal("-12.34"));

        this.mockMvc.perform(
                post("/v1/accounts/transfer/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isBadRequest());

        verify(accountsService, never()).transfer(any());
    }

    @Test
    public void shouldReturnNotAcceptableIfTransferResultsInException() throws Exception {
        Transfer transfer = new Transfer("Id-1", "Id-2", new BigDecimal("12.34"));

        doThrow(new BalanceTransferException("Transfer not allowed")).when(accountsService).transfer(transfer);

        this.mockMvc.perform(
                post("/v1/accounts/transfer/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isNotAcceptable());
    }
}
