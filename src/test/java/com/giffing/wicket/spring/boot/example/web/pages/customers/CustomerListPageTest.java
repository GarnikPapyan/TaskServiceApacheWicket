package com.giffing.wicket.spring.boot.example.web.pages.customers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.repeater.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.giffing.wicket.spring.boot.example.entity.Customer;

import com.giffing.wicket.spring.boot.example.web.WicketBaseTest;



public class CustomerListPageTest extends WicketBaseTest {

	private static final long CUSTOMERS_COUNT = 5;


	
	@Override
	@BeforeEach
	public void setUp(){
		super.setUp();
		

	}
	
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void assert_start_customer_list_page(){

		
		getTester().assertComponent("filterForm:table", DataTable.class);
		

		//get third row
		Item<Customer> item3 = (Item) getTester().getComponentFromLastRenderedPage("filterForm:table:body:rows:3");
		assertThat(item3.getModelObject().getId(), equalTo(3L));
		assertThat(item3.getModelObject().getUsername(), equalTo("username3"));
		
		Item<Customer> item5 = (Item) getTester().getComponentFromLastRenderedPage("filterForm:table:body:rows:5");
		assertThat(item5.getModelObject().getId(), equalTo(5L));
		assertThat(item5.getModelObject().getUsername(), equalTo("username5"));
	}
	
	@Test
	public void assert_click_customer_edit_page(){

		
		getTester().clickLink(getTableCell(5, 6) + "items:0:item:link");

		
	}
	
	@Test
	public void assert_click_customer_create_page(){

		
		getTester().clickLink("create");

	}
	
	@Test
	public void assert_delete_customer_method_called_once(){

		
		getTester().clickLink(getTableCell(5, 6) + "items:1:item:link");

		getTester().clickLink("defaultModal:overlay:dialog:content:yes", true);
		

		
	}
	
	private String getTableCell(int row, int cell){
		return "filterForm:table:body:rows:" + row + ":cells:" + cell + ":cell:";
	}
	
	public static List<Customer> createCustomers(long count) {
		List<Customer> customers = new ArrayList<>();
		for(long i = 1; i <= count; i++){
			customers.add(createCustomer(i));
		}
		return customers;
	}

	public static Customer createCustomer(long i) {
		Customer customer = new Customer();
		customer.setId(i);
		customer.setUsername("username" + i);
		customer.setFirstname("firstname" + i);
		customer.setLastname("lastname" + i);
		customer.setPassword("password" + i);
		return customer;
	}
	
}
