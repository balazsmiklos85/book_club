package hu.bmiklos.bc.view;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.linkbuilder.ILinkBuilder;

public class TestLinkBuilder implements ILinkBuilder {

    @Override
    public String getName() {
       return "TestLinkBuilder";
    }

    @Override
    public Integer getOrder() {
        return -1;
    }

    @Override
    public String buildLink(IExpressionContext context, String base, Map<String, Object> parameters) {
        return base + '?' +MapUtils.emptyIfNull(parameters)
            .entrySet()
            .stream()
            .map(entry -> entry.getKey() + '=' + emptyIfNull(entry.getValue()))
            .collect(Collectors.joining("&"));
    }

    private String emptyIfNull(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
