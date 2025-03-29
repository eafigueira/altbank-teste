package ia.altbank.controller;

import ia.altbank.dto.CreateCustomerRequest;
import ia.altbank.dto.CreateCustomerResponse;
import ia.altbank.service.CustomerService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class CustomerResource {

    private final CustomerService customerService;

    @POST
    public CreateCustomerResponse createCustomer(CreateCustomerRequest request) {
        return customerService.createCustomer(request);
    }
}
