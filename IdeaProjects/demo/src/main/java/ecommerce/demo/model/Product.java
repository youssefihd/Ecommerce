package ecommerce.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity @Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
    private String description;
    private String imgurl;
    private int inStock;
    private String Category;
    private String currency;
    @JsonIgnore
    @ManyToMany(mappedBy = "products")
    private List<Orderr> orders;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category; // Update to use Category entity instead of String

    // getters, setters, constructors
}