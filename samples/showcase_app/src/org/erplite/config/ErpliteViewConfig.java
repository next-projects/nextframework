package org.erplite.config;

import org.nextframework.core.config.DefaultViewConfig;
import org.nextframework.view.template.PropertyTag;
import org.springframework.stereotype.Component;

/**
 * Customizes how Next Framework renders views (forms, tables, properties).
 *
 * Auto-detected by Next Framework via ServiceFactory. Replaces DefaultViewConfig.
 */
@Component
public class ErpliteViewConfig extends DefaultViewConfig {

    @Override
    public Integer getDefaultColumns() {
        // 4 logical columns per row: 1 for label + 3 for input
        // With Bootstrap scaling: label = (1*12)/4 = col-md-3, input = (3*12)/4 = col-md-9
        return 4;
    }

    @Override
    public Integer getDefaultColspan(String renderAs) {
        // Total colspan for a DOUBLE property (label + input = 4 logical columns)
        // The label always takes 1, the input takes colspan - 1 = 3
        return 4;
    }

    @Override
    public String getDefaultPropertyRenderAs() {
        return PropertyTag.DOUBLE;
    }

}
