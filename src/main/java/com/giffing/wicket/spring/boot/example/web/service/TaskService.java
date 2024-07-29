package com.giffing.wicket.spring.boot.example.web.service;


import com.giffing.wicket.spring.boot.example.entity.Customer;
import com.giffing.wicket.spring.boot.example.entity.Status;
import com.giffing.wicket.spring.boot.example.entity.Tasks;
import com.giffing.wicket.spring.boot.example.repository.CustomerRepository;
import com.giffing.wicket.spring.boot.example.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class TaskService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TaskRepository taskRepository;

    public List<Status> getAllStatuses() {
        List<Tasks> tasks = taskRepository.findAll();
        Set<Status> statuses = new HashSet<>();
        for (Tasks task : tasks) {
            statuses.add(task.getStatus());
        }
        return new ArrayList<>(statuses);
    }
    @Transactional
    public List<Tasks> getAllTasks() {
        taskRepository.flush();
        return taskRepository.findAll();
    }


    public List<Tasks> getTasksByCustomerUserName(String customerUserName) {
        Customer customer = customerRepository.findCustomerByUsername(customerUserName);
        return taskRepository.findTasksByCustomerId(customer.getId());
    }

    public List<Tasks> getTasksByStatus(Status status) {
        return taskRepository.findByStatus(status);
    }
    @Transactional
    public void deleteTask(Tasks task) {
        taskRepository.deleteTasksById(task.getId());
    }
    @Transactional
    public List<Tasks> deleteTaskById(Long id) {

        System.out.println("deleting " + id);
        taskRepository.deleteById(id);
        return taskRepository.findAll();
    }
    public void saveTask(String taskName,String customerName,String taskDescription) {
        Tasks tasks = new Tasks();
        tasks.setCreationDate(LocalDateTime.now());
        tasks.setUpdateDate(LocalDateTime.now());
        tasks.setStatus(Status.NEW_TASK);

        tasks.setName(taskName);
        tasks.setDescription(taskDescription);
        tasks.setCustomer(customerRepository.findAllByUsername(customerName));
        taskRepository.save(tasks);
    }
    public void updateTask(Tasks task) {
        task.setUpdateDate(LocalDateTime.now());
        task.setCustomer(customerRepository.findCustomerById(task.getCustomer().getId()));
        taskRepository.save(task);
    }

    public void updateTaskStatus(Tasks task) {
        task.setUpdateDate(LocalDateTime.now());

        taskRepository.save(task);
    }

    public boolean hasThisStatusMoreOne(Status status) {
        int x = 0;
        List<Tasks> tasks = getTaskFromLoginUser();
        for (Tasks task : tasks) {
            if(task.getStatus().equals(status)) {
                x++;
            }
        }
        return x >=1;
    }

    public List<String> getCreationDate(){
        List<Tasks> tasks = getTaskFromLoginUser();
        List<String> creationDate = new ArrayList<>();
        for (Tasks task : tasks) {
            creationDate.add(task.getCreationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            creationDate.sort(String::compareTo);
        }
        return creationDate;
    }
    public List<Status> getStatusThisUser(){
        List<Tasks> tasks = getTaskFromLoginUser();
        Set<Status> statuses = new HashSet<>();
        for (Tasks task : tasks) {
            statuses.add(task.getStatus());
        }
        return new ArrayList<>(statuses);
    }

    public List<Tasks> getTaskFromLoginUser(){
        String loginUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Tasks> tasks = taskRepository.findTasksByCustomerId(customerRepository.findCustomerByUsername(loginUsername).getId());
        return tasks;
    }
    public List<Tasks> getAllTasksStatusFromLoginUser(Status status){
        List<Tasks> tasks = getTaskFromLoginUser();
        List<Tasks> tasksStatus = new ArrayList<>();
        for (Tasks task : tasks) {
            if (task.getStatus().equals(status)) {
                tasksStatus.add(task);
            }
        }
        return tasksStatus;
    }

    public List<Tasks> getAllTasksCreationDateFromLoginUser(String selectedDate){
        List<Tasks> tasks = getTaskFromLoginUser();
        List<Tasks> tasksDate= new ArrayList<>();
        for (Tasks task : tasks) {
            if (task.getCreationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).equals(selectedDate)) {
                tasksDate.add(task);
            }
        }
        return tasksDate;
    }

    public void saveTaskFromLoginUser(String taskName,String taskDescription) {
        Tasks task = new Tasks();
        String loginUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findCustomerByUsername(loginUsername);
        task.setCreationDate(LocalDateTime.now());
        task.setUpdateDate(LocalDateTime.now());
        task.setName(taskName);
        task.setDescription(taskDescription);
        task.setStatus(Status.NEW_TASK);
        task.setCustomer(customer);
        taskRepository.save(task);
    }

    public List<Tasks> getTasksFromUserNameAndStatus(String userName, Status status) {
        List<Tasks> tasks = getTasksByCustomerUserName(userName);
        List<Tasks> userAndStatus = new ArrayList<>();
        for (Tasks task : tasks) {
            if (task.getStatus().equals(status)) {
                userAndStatus.add(task);
            }
        }
        return userAndStatus;
    }

}
