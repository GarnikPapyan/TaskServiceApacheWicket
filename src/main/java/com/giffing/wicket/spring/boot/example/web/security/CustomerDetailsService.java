package com.giffing.wicket.spring.boot.example.web.security;

import com.giffing.wicket.spring.boot.example.entity.Customer;
import com.giffing.wicket.spring.boot.example.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


import java.util.Optional;

@Component
public class CustomerDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    @Autowired
    public CustomerDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customer = customerRepository.findByUsername(username);
        if (customer.isEmpty()) {
            System.out.println("Customer not found: First my step" );
            throw new UsernameNotFoundException("Customer not found");
        } else {
            System.out.println("First step - else");
            return new CustomerDetails(customer.get());
        }
    }
}
