package ecommerce.demo.controller;


public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String message) {
        super(message); // Pass the message to the superclass constructor
    }
}
