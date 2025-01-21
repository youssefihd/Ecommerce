package ecommerce.demo.controller;

import ecommerce.demo.model.Customer;
import ecommerce.demo.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:8082")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Get all customers
    @GetMapping()
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            // Log the exception to help with debugging
            System.err.println("Error fetching customers: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for more details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Get a customer by ID
    @GetMapping("/{id}")
    public Optional<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    // Create or update a customer
    @PostMapping()
    public ResponseEntity<String> createOrUpdateCustomer(@RequestBody Customer customer) {
        // Check if the customer already exists by email
        Optional<Customer> existingCustomer = customerService.getCustomerByEmail((customer.getEmail()));

        if (existingCustomer.isPresent()) {
            if (!existingCustomer.get().getId().equals(customer.getId())) {
                // Email already exists for a different customer
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Email already exists, cannot update.");
            }
            // Update the existing customer
            customerService.saveCustomer(customer);
            return ResponseEntity.ok("Customer updated successfully.");
        } else {
            // Create a new customer
            customerService.saveCustomer(customer);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Customer created successfully.");
        }
    }

    @GetMapping("/email/{email}")
    public Optional<Customer> getCustomerByEmail(@PathVariable String email) {
        return customerService.getCustomerByEmail(email);
    }

    // Delete a customer by ID
    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}
