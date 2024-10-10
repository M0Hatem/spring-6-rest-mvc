package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.CustomerService;
import guru.springframework.spring6restmvc.services.CustomerServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest
class CustomerControllerTest {

    @MockBean
    BeerService beerService;

    @MockBean
    CustomerService customerService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    CustomerServiceImpl customerServiceImpl;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<CustomerDTO> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }


    @Test
    void patchCustomerById() throws Exception {
        CustomerDTO customer = customerServiceImpl.getAllCustomers().get(0);

        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("name", "New Name");

        mockMvc.perform(patch( CustomerController.CUSTOMER_PATH_ID, customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerMap)))
                .andExpect(status().isNoContent());

        verify(customerService).patchCustomerById(uuidArgumentCaptor.capture(),
                customerArgumentCaptor.capture());

        Assertions.assertThat(uuidArgumentCaptor.getValue()).isEqualTo(customer.getId());
        Assertions.assertThat(customerArgumentCaptor.getValue().getName())
                .isEqualTo(customerMap.get("name"));
    }

    @Test
    void deleteCustomerById() throws Exception {
        CustomerDTO customer = customerServiceImpl.getAllCustomers().getFirst();

        mockMvc.perform(delete(CustomerController.CUSTOMER_PATH_ID,customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        uuidArgumentCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(customerService).deleteCustomerById(uuidArgumentCaptor.capture());
        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());

    }

    @Test
    void updateCustomerByID() throws Exception {
        CustomerDTO updatedCustomer = customerServiceImpl.getAllCustomers().getFirst();

        mockMvc.perform(put(CustomerController.CUSTOMER_PATH_ID,updatedCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isNoContent());
        verify(customerService).updateCustomerById(any(UUID.class),any(CustomerDTO.class));
    }

    @Test
    void handlePost() throws Exception {
        CustomerDTO customer = customerServiceImpl.getAllCustomers().getFirst();
        customer.setId(null);
        customer.setVersion(null);

        given(customerService.saveNewCustomer(customer)).willReturn(customerServiceImpl.getAllCustomers().get(1));

        mockMvc.perform(post(CustomerController.CUSTOMER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void listAllCustomers() throws Exception {
        given(customerService.getAllCustomers()).willReturn(customerServiceImpl.getAllCustomers());

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()",is(3)));

    }

    @Test
    void getCustomerByIdNotFound() throws Exception {
        given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCustomerById() throws Exception {
        CustomerDTO customer = customerServiceImpl.getAllCustomers().getFirst();

        given(customerService.getCustomerById(customer.getId())).willReturn(Optional.of(customer));

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(customer.getId().toString())))
                .andExpect(jsonPath("$.name",is(customer.getName().toString())));

    }
}