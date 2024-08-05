package com.giffing.wicket.spring.boot.example.web.service;

import com.giffing.wicket.spring.boot.example.entity.Customer;
import com.giffing.wicket.spring.boot.example.entity.Roles;
import com.giffing.wicket.spring.boot.example.entity.Status;
import com.giffing.wicket.spring.boot.example.entity.Tasks;
import com.giffing.wicket.spring.boot.example.repository.CustomerRepository;
import com.giffing.wicket.spring.boot.example.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Transactional
    public void save(String firstName, String lastName, String userName, String password) {
        Customer savedCustomer = new Customer();
        String encodedPassword = passwordEncoder.encode(password);
        savedCustomer.setFirstname(firstName);
        savedCustomer.setLastname(lastName);
        savedCustomer.setUsername(userName);
        savedCustomer.setPassword(encodedPassword);
        savedCustomer.setRoles(Roles.EMPLOYEE);
        customerRepository.save(savedCustomer);
    }

    public Optional<Customer> findByUsername(String username) {
        System.out.println("repoic ekav " + username);
        return customerRepository.findByUsername(username);
    }


    @Transactional
    public List<String> getAllUsernames() {
        List<Customer> customers = customerRepository.findAll();
        List<String> usernames = new ArrayList<>();
        for (Customer customer : customers) {
            if(customer.getRoles()!= Roles.MANAGER) {
                usernames.add(customer.getUsername());
            }
        }
        return usernames;
    }

    @Transactional
    public void deleteTaskFromCustomer(Tasks task) {
        Customer customer = task.getCustomer();
        List<Tasks> list = customer.getTasks();
        list.remove(task);
    }

    public String findById(Long id) {
        return customerRepository.findById(id).get().getUsername();
    }
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
    public Boolean existsByUsername(String username) {
        return customerRepository.existsCustomerByUsername(username);
    }

    public String getCustomerSecretKay(String customerName){
        Optional<Customer> customer =customerRepository.findByUsername(customerName);
        return customer.get().getSecretKay();
    }
}
