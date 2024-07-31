package com.giffing.wicket.spring.boot.example.web.pages.login;

import com.giffing.wicket.spring.boot.example.entity.Roles;
import com.giffing.wicket.spring.boot.example.web.pages.home.Employee;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.wicketstuff.annotation.mount.MountPath;

import com.giffing.wicket.spring.boot.context.scan.WicketSignInPage;

import com.giffing.wicket.spring.boot.example.web.pages.BasePage;
import com.giffing.wicket.spring.boot.example.web.pages.home.HomePage;


@WicketSignInPage
@MountPath("login")
public class LoginPage extends BasePage {

	@SpringBean
	private AuthenticationManager authenticationManager;
	@SpringBean
	private AuthenticationProvider authenticationProvider;


	public LoginPage(PageParameters parameters) {
		super(parameters);
		if (((AbstractAuthenticatedWebSession) getSession()).isSignedIn()) {
			continueToOriginalDestination();
			setResponsePage(HomePage.class);
		}


		add(new LoginForm("loginForm"));

	}

	private class LoginForm extends StatelessForm<LoginForm> {

		private String username;
		
		private String password;
		private final ValueMap properties = new ValueMap();

		private TextField<String> usernameTextField;
		private PasswordTextField passwordTextField;

		public LoginForm(String id) {
			super(id);
		//	setModel(new CompoundPropertyModel<>(this));
			usernameTextField = new TextField<>("username", new PropertyModel<>(properties, "username"));
			usernameTextField.setOutputMarkupId(true);
			usernameTextField.setRequired(true);
			add(usernameTextField);
			passwordTextField = new PasswordTextField("password", new PropertyModel<>(properties, "password"));
			passwordTextField.setRequired(true);
			passwordTextField.setOutputMarkupId(true);
			add(passwordTextField);
			add(new FeedbackPanel("feedback"));

		}

		@Override
		protected void onSubmit() {
			System.out.println("On submit");
			username = properties.getString("username");
			password = properties.getString("password");
			System.out.println("username: " + username + " password: " + password);
			System.out.println();
			try {
				if(username != null && password != null){
					Authentication authentication = authenticationManager.authenticate(
							new UsernamePasswordAuthenticationToken(username, password)
					);
					if(authentication.isAuthenticated()) {
						System.out.println("mtav normalaaaa");
						if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("MANAGER"))){
							AuthenticatedWebSession session = AuthenticatedWebSession.get();
							session.signIn(username,password);
							System.out.println(authentication.getAuthorities().contains(new SimpleGrantedAuthority("MANAGER")));
							System.out.println(authentication.getAuthorities().contains(new SimpleGrantedAuthority("EMPLOYEE")));
							setResponsePage(HomePage.class);
						} else if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("EMPLOYEE"))) {
							AuthenticatedWebSession session = AuthenticatedWebSession.get();
							session.signIn(username,password);
							System.out.println(authentication.getAuthorities().contains(new SimpleGrantedAuthority("MANAGER")));
							System.out.println(authentication.getAuthorities().contains(new SimpleGrantedAuthority("EMPLOYEE")));
							setResponsePage(Employee.class);
						}
					} else {
						System.out.println("sxal pass");
						info("Login failed");
					}
				}
			} catch (AuthenticationException e) {
				error("Login failed");
			}
		}

		@Override
		protected void onError() {
			System.out.println("---------------------------------------------------------------------------------------");
			System.out.println("REQUIRED");
			System.out.println("---------------------------------------------------------------------------------------");
		}

	}
}
