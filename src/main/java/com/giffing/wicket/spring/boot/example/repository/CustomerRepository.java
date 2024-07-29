package com.giffing.wicket.spring.boot.example.repository;

import com.giffing.wicket.spring.boot.example.entity.Customer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);
    Long findIdByUsername(String username);
    Customer findAllByUsername(String username);
    Customer findCustomerById(Long id);
//    Long findIdByUsername(String username);
    Customer findCustomerByUsername(String username);

}
