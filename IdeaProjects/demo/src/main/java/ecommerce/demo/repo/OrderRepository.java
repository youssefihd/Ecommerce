package ecommerce.demo.repo;

import ecommerce.demo.model.Customer;
import ecommerce.demo.model.Orderr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface OrderRepository extends JpaRepository<Orderr,Long> {

}