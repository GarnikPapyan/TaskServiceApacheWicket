package com.giffing.wicket.spring.boot.example.web.pages.customers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.repeater.Item;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.giffing.wicket.spring.boot.example.entity.Customer;

import com.giffing.wicket.spring.boot.example.web.WicketBaseIntTest;


@Transactional
@Rollback
public class CustomerListIntTest extends WicketBaseIntTest {

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void assert_start_customer_list_page() {

        getTester().assertComponent("filterForm:table", DataTable.class);

        //get third row
        Item<Customer> item3 = (Item) getTester().getComponentFromLastRenderedPage("filterForm:table:body:rows:3");
        assertThat(item3.getModelObject().getId(), equalTo(3L));
        assertThat(item3.getModelObject().getUsername(), equalTo("adalgrim"));

        Item<Customer> item5 = (Item) getTester().getComponentFromLastRenderedPage("filterForm:table:body:rows:5");
        assertThat(item5.getModelObject().getId(), equalTo(5L));
        assertThat(item5.getModelObject().getUsername(), equalTo("tuk"));
    }

    @Test
    public void assert_delete_customer_method_called_once() {



        getTester().clickLink(getTableCell(5, 6) + "items:1:item:link");

        getTester().clickLink("defaultModal:overlay:dialog:content:yes", true);


    }

    private String getTableCell(int row, int cell) {
        return "filterForm:table:body:rows:" + row + ":cells:" + cell + ":cell:";
    }
}
