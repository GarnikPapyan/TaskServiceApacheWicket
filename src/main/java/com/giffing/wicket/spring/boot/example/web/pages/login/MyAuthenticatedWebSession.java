package com.giffing.wicket.spring.boot.example.web.pages.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


public class MyAuthenticatedWebSession extends AuthenticatedWebSession {

    private UserDetails userDetails;
    private HttpSession httpSession;
    @SpringBean
    private AuthenticationManager authenticationManager;

    public MyAuthenticatedWebSession(Request request) {
        super(request);
        this.httpSession = ((HttpServletRequest) request.getContainerRequest()).getSession();
        Injector.get().inject(this);
    }

    @Override
    protected boolean authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println((userDetails != null) + " esi el es mi bloky");
        return userDetails != null;
    }

    public static MyAuthenticatedWebSession get() {
        return (MyAuthenticatedWebSession) Session.get();
    }



    @Override
    public Roles getRoles() {
        if(isSignedIn()){
            String username = userDetails.getUsername();
            if ("manager".equals(username.toLowerCase())) {
                return new Roles("MANAGER");
            } else return new Roles("EMPLOYEE");
        }
        return  null;
    }

    @Override
    public void signOut() {
        super.signOut();
        userDetails = null;
    }

    public boolean isAuthenticated() {
        return userDetails != null;
    }
}
