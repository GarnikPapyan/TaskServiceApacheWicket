package com.giffing.wicket.spring.boot.example.web.pages.customers.create;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.apache.wicket.util.tester.FormTester;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.giffing.wicket.spring.boot.example.entity.Customer;

import com.giffing.wicket.spring.boot.example.web.WicketBaseTest;


public class CustomerCreatePageTest extends WicketBaseTest {


	
	@Override
	@BeforeEach
	public void setUp(){
		super.setUp();
	}
	
	@Test
	public void assert_customer_save_called(){


		FormTester formTester = getTester().newFormTester("form");
		formTester.setValue(borderPath("username"), "myUsername");
		formTester.setValue(borderPath("firstname"), "myFirstname");
		formTester.setValue(borderPath("lastname"), "myLastname");
		formTester.setValue(borderPath("password"), "myPassword");
		formTester.submit("submit");
		getTester().assertNoErrorMessage();
		getTester().assertNoInfoMessage();

		
		ArgumentCaptor<Customer> customerArgument = ArgumentCaptor.forClass(Customer.class);
		

		
		Customer value = customerArgument.getValue();
		assertThat(value.getId(), nullValue());
		assertThat(value.getUsername(), equalTo("myUsername"));
		assertThat(value.getFirstname(), equalTo("myFirstname"));
		assertThat(value.getLastname(), equalTo("myLastname"));
		assertThat(value.getPassword(), equalTo("myPassword"));
	}
	
	@Test
	public void assert_customer_not_saved_if_user_already_exist() {


		String newUsername = "newUsername";


		
		FormTester formTester = getTester().newFormTester("form");
		formTester.setValue(borderPath("username"), newUsername);
		formTester.setValue(borderPath("firstname"), "myFirstname");
		formTester.setValue(borderPath("lastname"), "myLastname");
		formTester.setValue(borderPath("password"), "myPassword");
		formTester.submit("submit");
		
		getTester().assertErrorMessages("Username already exists!");

		
		//it should be checked, that the username does already exists

	}
	
	private String borderPath(String componentName){
		return componentName + "Border:" + componentName + "Border_body:" + componentName;
	}

}
