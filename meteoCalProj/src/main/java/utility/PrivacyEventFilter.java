package utility;

import EJB.interfaces.EventManager;
import bakingBeans.LoginBacking;
import java.io.IOException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
import model.PrivateEvent;
import model.PublicEvent;

/**
 *
 * @author Francesco
 */
public class PrivacyEventFilter implements Filter {

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
        System.out.println("-dentro PrivacyEventFilter");
        // check whether we have a httpServletRequest and a pattern
        if (this.pattern != null && request instanceof HttpServletRequest) {
            // resolve the query string from the httpServletRequest
            String uri = ((HttpServletRequest) request).getRequestURI();
            // check whether a query string exists and check if it starts with the pattern
            if (uri != null && uri.endsWith(pattern)) {
                //risalgo all'evento
                String eventId = ((HttpServletRequest) request).getParameter(
                        "id");
                if (eventId != null) {
                    try {
                        Event ev = eventManager.findEventbyId(Long.parseLong(
                                eventId));
                        if (ev != null) {
                            //se l'evento non è pubblico o è privato e sei l owner o hai un invito
                            if (!(ev instanceof PublicEvent)
                                    && !((ev instanceof PrivateEvent)
                                    && (ev.getOwner().equals(
                                            login.getCurrentUser())
                                    || (ev.getInvitee().contains(
                                            login.getCurrentUser()))))) {
                                ((HttpServletResponse) response).sendError(
                                        HttpServletResponse.SC_FORBIDDEN,
                                        "You are not allowed to view this event");
                                error = true;
                            }
                        }
                    } catch (NumberFormatException ex) {
                        ((HttpServletResponse) response).sendError(
                                HttpServletResponse.SC_BAD_REQUEST,
                                "The event does not exist.");
                        error = true;
                    }
                } else {
                    ((HttpServletResponse) response).sendError(
                            HttpServletResponse.SC_BAD_REQUEST,
                            "The event does not exist.");
                    error = true;
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
