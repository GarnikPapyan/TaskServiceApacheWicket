package com.giffing.wicket.spring.boot.example.web.pages.login;

import com.giffing.wicket.spring.boot.example.web.pages.BasePage;
import com.giffing.wicket.spring.boot.example.web.pages.home.Employee;
import com.giffing.wicket.spring.boot.example.web.service.CustomerService;
import com.giffing.wicket.spring.boot.example.web.service.TaskService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("/register")
public class RegistrationPage extends BasePage {

    @SpringBean
    private CustomerService customerService;
    @SpringBean
    private TaskService taskService;

    final Form<Void> form;
    private final ValueMap properties;
    private TextField<String> firstNameTextField;
    private TextField<String> lastNameTextField;
    private TextField<String> userNameTextField;
    private PasswordTextField passwordTextField;
    public RegistrationPage() {
      form = new Form<>("form");
      form.setOutputMarkupId(true);
      add(form);
      FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
      feedbackPanel.setOutputMarkupId(true);
      form.add(feedbackPanel);
      properties = new ValueMap();
      firstNameTextField = new TextField<>("firstName", new PropertyModel(properties, "firstName"));
      firstNameTextField.setOutputMarkupId(true);
      firstNameTextField.setRequired(true);
      lastNameTextField = new TextField<>("lastName", new PropertyModel(properties, "lastName"));
      lastNameTextField.setOutputMarkupId(true);
      lastNameTextField.setRequired(true);
      userNameTextField  = new TextField<>("userName", new PropertyModel(properties, "userName"));
      userNameTextField.setOutputMarkupId(true);
      userNameTextField.setRequired(true);
      passwordTextField = new PasswordTextField("password", new PropertyModel(properties, "password"));
      passwordTextField.setOutputMarkupId(true);
      passwordTextField.setRequired(true);
      form.add(firstNameTextField);
      form.add(lastNameTextField);
      form.add(userNameTextField);
      form.add(passwordTextField);
      form.add(new AjaxButton("createButton") {

          @Override
          protected void onSubmit(AjaxRequestTarget target) {
              super.onSubmit(target);
              String userName = properties.getString("userName");
              if (customerService.existsByUsername(userName)) {
                  info("Customer with name " + userName + " already exists");
                  target.add(feedbackPanel);
              }else {
                  String firstname = properties.getString("firstName");
                  String lastName = properties.getString("lastName");
                  String password = properties.getString("password");
                  System.out.println("name = " + firstname +"  lastName = " + lastName+ "  userName " + userName  + " password " + password);


                  customerService.save(firstname, lastName, userName, password);
                  AuthenticatedWebSession session = AuthenticatedWebSession.get();
                  session.signIn(userName,password);

                  setResponsePage(Employee.class);
              }
          }
      });

    }
}
