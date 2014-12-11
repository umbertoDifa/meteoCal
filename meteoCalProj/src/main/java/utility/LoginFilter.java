
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;

import bakingBeans.LoginBacking;
import java.io.IOException;
import javax.inject.Inject;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Filter checks if LoginBean has loginIn property set to true. If it is not set
 * then request is being redirected to the login.xhml page.
 *
 * @author itcuties
 *
 */
@WebFilter("/s/*")
public class LoginFilter implements Filter {

    @Inject
    LoginBacking login;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

//        LoginBacking login = (LoginBacking) ((HttpServletRequest) request).getSession().getAttribute("login");

        if (login != null && login.isLoggedIn()) {
            chain.doFilter(request, response);
        } else {
            String contextPath = ((HttpServletRequest) request).getContextPath();
            ((HttpServletResponse) response).sendRedirect(contextPath + "/signUp.xhtml");
        }

    }

    public void init(FilterConfig config) throws ServletException {
        // Nothing to do here!
    }

    public void destroy() {
        // Nothing to do here!
    }

//    public class CharResponseWrapper extends HttpServletResponseWrapper {
//
//        private CharArrayWriter output;
//
//        @Override
//        public String toString() {
//            return output.toString();
//        }
//
//        public CharResponseWrapper(HttpServletResponse response) {
//            super(response);
//            output = new CharArrayWriter();
//        }
//
//        @Override
//        public PrintWriter getWriter() {
//            return new PrintWriter(output);
//        }
//    }

}
