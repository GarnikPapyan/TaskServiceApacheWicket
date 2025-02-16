package com.giffing.wicket.spring.boot.example.web.pages.customers.create;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


import org.apache.wicket.util.tester.FormTester;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.giffing.wicket.spring.boot.example.entity.Customer;

import com.giffing.wicket.spring.boot.example.web.WicketBaseIntTest;


@Transactional
@Rollback
public class CustomerCreatePageIntTest extends WicketBaseIntTest {
	

	
	@Test
	public void assert_new_customer_saved(){
		

		FormTester formTester = getTester().newFormTester("form");
		
		String usernameOfNewCreatedUser = "newUser";
		formTester.setValue(borderPath("username"), usernameOfNewCreatedUser);
		formTester.setValue(borderPath("firstname"), "newFirstname");
		formTester.setValue(borderPath("lastname"), "newLastname");
		formTester.setValue(borderPath("password"), "newPassword");
		formTester.submit("submit");
		getTester().assertNoErrorMessage();
		getTester().assertNoInfoMessage();




	}
	
	@Test
	public void assert_error_when_create_existing_customer(){

		getTester().debugComponentTrees();
		FormTester formTester = getTester().newFormTester("form");
		
		//user is created via liquibase as initial data
		String usernameOfExistingUser = "frodo";
		formTester.setValue(borderPath("username"), usernameOfExistingUser);
		formTester.setValue(borderPath("firstname"), "newFirstname");
		formTester.setValue(borderPath("lastname"), "newLastname");
		formTester.setValue(borderPath("password"), "newPassword");
		formTester.submit("submit");
		
		getTester().assertErrorMessages("Username already exists!");
		
	}
	
	private String borderPath(String componentName){
		return componentName + "Border:" + componentName + "Border_body:" + componentName;
	}

}
