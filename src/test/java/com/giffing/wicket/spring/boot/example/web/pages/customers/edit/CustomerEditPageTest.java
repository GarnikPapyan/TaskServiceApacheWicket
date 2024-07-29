package com.giffing.wicket.spring.boot.example.web.pages.customers.edit;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.giffing.wicket.spring.boot.example.entity.Customer;

import com.giffing.wicket.spring.boot.example.web.WicketBaseTest;

import com.giffing.wicket.spring.boot.example.web.pages.customers.CustomerListPageTest;


public class CustomerEditPageTest extends WicketBaseTest {

	private static final Long CUSTOMERS_COUNT = 5L;

	
	@Override
	@BeforeEach
	public void setUp(){
		super.setUp();

		
	}
	
	
	@Test
	public void assert_customer_not_exists(){
		PageParameters params = new PageParameters();

		
		
		getTester().assertErrorMessages("Customer not found 9548");
		//TODO how to get a resource from a page which can't be accessed cause of redirect.
//		Localizer localizer = getTester().getApplication().getResourceSettings()
//                .getLocalizer();
//		
//		getTester().assertErrorMessages(MessageFormat.format(localizer.getString("customer.not-found", getTester().getLastRenderedPage()), "9548"));
	}
	
	@Test
	public void assert_customer_on_load_existing(){
		PageParameters params = new PageParameters();

		getTester().assertNoErrorMessage();
		getTester().assertNoInfoMessage();
		

		
		FormTester formTester = getTester().newFormTester("form");
		
		String username = formTester.getTextComponentValue(borderPath("username"));
		String firstname = formTester.getTextComponentValue(borderPath("firstname"));
		String lastname = formTester.getTextComponentValue(borderPath("lastname"));
		
		assertThat(username, equalTo("username3"));
		assertThat(firstname, equalTo("firstname3"));
		assertThat(lastname, equalTo("lastname3"));
		
		getTester().debugComponentTrees();
		
		String usernameFieldPath = "form:" + borderPath("username");

		getTester().isDisabled(usernameFieldPath);
		
		String passwordFieldPath = "form:" + borderPath("password");
		getTester().isInvisible(passwordFieldPath);
	}
	
	@Test
	public void assert_customer_saved(){
		PageParameters params = new PageParameters();


		FormTester formTester = getTester().newFormTester("form");
		formTester.setValue(borderPath("firstname"), "the-new-firstname");
		formTester.submit("submit");
		
		ArgumentCaptor<Customer> customerArgument = ArgumentCaptor.forClass(Customer.class);

		
		Customer value = customerArgument.getValue();
		assertThat(value.getId(), equalTo(3L));
		assertThat(value.getUsername(), equalTo("username3"));
		assertThat(value.getFirstname(), equalTo("the-new-firstname"));
		assertThat(value.getLastname(), equalTo("lastname3"));
		assertThat(value.getPassword(), equalTo("password3"));
		
		
	}
	
	private String borderPath(String componentName){
		return componentName + "Border:" + componentName + "Border_body:" + componentName;
	}

}
