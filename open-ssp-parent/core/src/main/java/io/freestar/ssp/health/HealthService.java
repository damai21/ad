package io.freestar.ssp.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Servlet implementation class HealthService
 * 
 * @author Brian Sorensen
 */
@WebServlet(value = "/health", asyncSupported = false, name = "Health-Service")
public class HealthService extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Logger log = LoggerFactory.getLogger(HealthService.class);
    private final Runtime runtime;
    private String json = "{\"status\": \"healthy\"}";

    public HealthService()
    {
        runtime = Runtime.getRuntime();
    }

    @Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        System.out.println("*****HealthService:");
        response.addHeader("Content-Type", "application/json");
        try (PrintWriter out = response.getWriter()) {
            out.println(json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        response.setStatus(200);
        log.debug("health check called");
        System.out.println("Memory: "+(runtime.freeMemory()/1024d/1000d/1000d)+":"+(runtime.maxMemory()/1024d/1000d/1000d)+":"+(runtime.totalMemory()/1024d/1000d/1000d));
        System.gc();
        System.out.println(".****HealthService:");
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		doGet(request, response);
	}

}
