package hu.bmiklos.bc.view;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.Set;

import org.thymeleaf.context.Context;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;

public class TestContext implements IWebContext {
    private final IWebExchange exchange;
    private final Context innerContext;
    private final IWebRequest request;

    public TestContext() {
        exchange = mock(IWebExchange.class);
        innerContext = new Context();
        request = mock(IWebRequest.class);

        when(exchange.getRequest()).thenReturn(request);
        when(request.getApplicationPath()).thenReturn("server.name.integration.test.hu/");
    }

    @Override
    public IWebExchange getExchange() {
        return exchange;
    }

    @Override
    public Locale getLocale() {
        return innerContext.getLocale();
    }

    @Override
    public boolean containsVariable(String name) {
        return innerContext.containsVariable(name);
    }

    @Override
    public Set<String> getVariableNames() {
        return innerContext.getVariableNames();
    }

    @Override
    public Object getVariable(String name) {
        return innerContext.getVariable(name);
    }

    public void setVariable(String name, Object value) {
        innerContext.setVariable(name, value);
    }
}
