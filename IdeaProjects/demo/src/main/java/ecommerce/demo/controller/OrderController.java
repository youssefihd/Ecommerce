package ecommerce.demo.controller;

import ecommerce.demo.model.Customer;
import ecommerce.demo.model.Orderr;
import ecommerce.demo.model.Product;
import ecommerce.demo.model.Status;
import ecommerce.demo.repo.CustomerRepository;
import ecommerce.demo.repo.ProductRepository;
import ecommerce.demo.service.CustomerService;
import ecommerce.demo.service.OrderService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:8082")
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;

    public OrderController(OrderService orderService, CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    // Get all orders
    @GetMapping
    public List<Orderr> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Get an order by ID
    @GetMapping("/{id}")
    public Optional<Orderr> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // Create or update an order
    @PostMapping
    public ResponseEntity<Orderr> createOrUpdateOrder(@RequestBody Orderr order) {
        try {
            // Validate customer ID
            Optional<Customer> customerOpt = customerService.getCustomerById(order.getCustomer().getId());
            if (customerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            order.setCustomer(customerOpt.get());
            Orderr savedOrder = orderService.saveOrder(order);
            return ResponseEntity.ok(savedOrder);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
    @PutMapping("/api/orders/{id}")
    public ResponseEntity<Orderr> updateOrder(@PathVariable Long id, @RequestBody Orderr orderDetails) {
        // Fetch the existing order by ID
        Optional<Orderr> existingOrderOptional = orderService.getOrderById(id);
        if (!existingOrderOptional.isPresent()) {
            return ResponseEntity.notFound().build(); // Order not found
        }

        // Get the existing order
        Orderr existingOrder = existingOrderOptional.get();

        // Update the fields of the existing order with values from orderDetails
        if (orderDetails.getProducts() != null) {
            existingOrder.setProducts(orderDetails.getProducts());
        }
        if (orderDetails.getQnt() != null) {
            existingOrder.setQnt(orderDetails.getQnt());
        }

        // Update order status
        if (orderDetails.getStatus() != null) {
            existingOrder.setStatus(orderDetails.getStatus());
        }

        // Save the updated order
        Orderr updatedOrder = orderService.saveOrder(existingOrder);
        return ResponseEntity.ok(updatedOrder); // Return the updated order
    }
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Orderr> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> request) {
        String statusString = request.get("status");
        Status status;

        try {
            status = Status.valueOf(statusString); // Convert string to Status enum
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Invalid status
        }

        Orderr updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }



}