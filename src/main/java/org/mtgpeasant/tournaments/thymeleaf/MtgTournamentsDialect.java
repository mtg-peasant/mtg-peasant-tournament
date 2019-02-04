package org.mtgpeasant.tournaments.thymeleaf;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.Collections;
import java.util.Set;

/**
 * Thymeleaf Dialect extension that adds mtg-tournaments specific messaging utils
 */
public class MtgTournamentsDialect implements IExpressionObjectDialect {
    @Override
    public String getName() {
        return "mtg-tournaments";
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return new IExpressionObjectFactory() {
            @Override
            public Set<String> getAllExpressionObjectNames() {
                return Collections.singleton("auth");
            }

            @Override
            public Object buildObject(IExpressionContext eExpressionContext, String expressionObjectName) {
//                Locale locale = eExpressionContext.getLocale();
                return new AuthenticationUtils();
            }

            @Override
            public boolean isCacheable(String s) {
                return true;
            }
        };
    }
}
