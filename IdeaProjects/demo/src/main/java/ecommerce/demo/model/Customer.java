package ecommerce.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity @Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;
    private String number;
    private String adresse;

    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private List<Orderr> orders;

    // getters, setters, constructors
}