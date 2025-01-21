package ecommerce.demo.service;

import ecommerce.demo.model.Orderr;
import ecommerce.demo.model.Product;
import ecommerce.demo.model.Status;
import ecommerce.demo.repo.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
     @Autowired

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Create or update an order
    public Orderr saveOrder(Orderr order) {
        // Validate order quantities against product stock
        return orderRepository.save(order);
    }


    // Get all orders
    public List<Orderr> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get order by ID
    public Optional<Orderr> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // Delete an order
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
    public Orderr updateOrderStatus(Long orderId, Status status) {
        Orderr order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        order.setStatus(status); // Set the new status
        return orderRepository.save(order); // Save the updated order
    }
}
