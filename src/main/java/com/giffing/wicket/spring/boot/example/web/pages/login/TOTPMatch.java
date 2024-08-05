package com.giffing.wicket.spring.boot.example.web.pages.login;

import com.giffing.wicket.spring.boot.example.totp.TOTPAuthenticator;
import com.giffing.wicket.spring.boot.example.web.pages.BasePage;
import com.giffing.wicket.spring.boot.example.web.pages.home.Employee;
import com.giffing.wicket.spring.boot.example.web.pages.home.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.springframework.security.core.context.SecurityContextHolder;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("/auth")
public class TOTPMatch extends BasePage {
//    public static String secretKey = "2WIIK56LJ64UMRKO27GQVWYM5XLMQVXU";
    public static Boolean TOTP_FIELD = false;
    public static String secretKey;
    final Form<Void> form;
    private final ValueMap properties;
    private TextField<String> authCode;
    static String authCodeValue = null;
    public TOTPMatch() {
        form = new Form<>("form");
        form.setOutputMarkupId(true);
        add(form);
        properties = new ValueMap();
        authCode = new TextField<>("authCode", new PropertyModel<String>(properties, "authCode"));
        authCode.setRequired(true);
        authCode.setOutputMarkupId(true);
        FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);
        form.add(authCode);
        form.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                 authCodeValue = properties.getString("authCode");
                 if(authCodeValue.equals(TOTPAuthenticator.getTOTPCode(secretKey)) && LoginPage.flag.equals("employee")){
                     TOTP_FIELD = true;
                     System.out.println(authCodeValue + " is true code");
                     setResponsePage(Employee.class);
                     TOTP_FIELD = false;
                 }  else if(authCodeValue.equals(TOTPAuthenticator.getTOTPCode(secretKey)) && LoginPage.flag.equals("manager")){
                     TOTP_FIELD = true;
                     System.out.println(authCodeValue + " is true code");
                     setResponsePage(HomePage.class);
                     TOTP_FIELD = false;
                 }
                 else {
                     info("fail auth number try");
                     target.add(feedbackPanel);
                     TOTP_FIELD = false;
                 }
            }
        });
        form.add(new AjaxLink<>("backLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                AuthenticatedWebSession session = AuthenticatedWebSession.get();
                session.invalidate();
                session.signOut();
                SecurityContextHolder.clearContext();
                setResponsePage(LoginPage.class);
                setResponsePage(HomePage.class);
            }
        });
    }
}
