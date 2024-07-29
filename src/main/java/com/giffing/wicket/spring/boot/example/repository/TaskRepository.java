package com.giffing.wicket.spring.boot.example.repository;

import com.giffing.wicket.spring.boot.example.entity.Customer;
import com.giffing.wicket.spring.boot.example.entity.Status;
import com.giffing.wicket.spring.boot.example.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Tasks, Long> {
    List<Tasks> findByStatus(Status status);
    List<Tasks> findTasksByCustomerId(Long customerId);
    List<Tasks> findTasksByCustomer(Customer customer);
    void deleteTasksById(Long id);

}
