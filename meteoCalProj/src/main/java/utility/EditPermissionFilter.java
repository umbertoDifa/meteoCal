package utility;

/**
 *
 * @author Francesco
 */
import EJB.interfaces.EventManager;
import bakingBeans.LoginBacking;
import java.io.IOException;
import java.util.logging.Logger;
import javax.inject.Inject;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Event;

public class EditPermissionFilter implements Filter {

    private String pattern;

    @Inject
    EventManager eventManager;

    @Inject
    LoginBacking login;

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        boolean error = false;
        // check whether we have a httpServletRequest and a pattern
        if (this.pattern != null && request instanceof HttpServletRequest) {
            // resolve the query string from the httpServletRequest
            String uri = ((HttpServletRequest) request).getRequestURI();
            // check whether a query string exists and check if it starts with the pattern
            if (uri != null && uri.endsWith(pattern)) {
                // salvo il parametro idEvent
                String eventId = ((HttpServletRequest) request).getParameter("id");
                // se c è
                if (eventId != null) {
                    // cerco evento corrispondente
                    Event ev = eventManager.findEventbyId(Long.parseLong(eventId));
                    // se c è
                    if (ev != null) {
                        // se l utente non è l owner
                        if (!login.getCurrentUser().equals(ev.getOwner())) {
                            // sollevo un errore 403
                            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to manage this event");
                            error = true;
                        }
                    } else {
                        // se non c è sollevo un errore 404
                        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_NOT_FOUND, "No event found");
                        error = true;
                    }
                }
            }
        }
        if (error == false) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.pattern = filterConfig.getInitParameter("pattern");
    }

}
