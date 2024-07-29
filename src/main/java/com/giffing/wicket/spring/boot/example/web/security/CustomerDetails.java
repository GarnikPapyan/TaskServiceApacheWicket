package com.giffing.wicket.spring.boot.example.web.security;

import com.giffing.wicket.spring.boot.example.entity.Customer;
import org.springframework.security.core.authority.AuthorityUtils;




public class CustomerDetails extends org.springframework.security.core.userdetails.User {

    private Customer customer;

    public CustomerDetails(Customer customer) {
        super(customer.getUsername(), customer.getPassword(), AuthorityUtils.createAuthorityList(customer.getRoles().toString())); //"ROLE_CUSTOMER"
        this.customer = customer;
    }
    public Customer getCustomers() {
        return customer;
    }
}
