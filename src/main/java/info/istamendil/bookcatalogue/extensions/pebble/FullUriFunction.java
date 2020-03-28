package info.istamendil.bookcatalogue.extensions.pebble;

import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//ToDo: Add more functionality like adding host, schema.

/**
 * Function for generating urls with context etc.
 * Differ from Symfony Twig function due specific Java ideas.
 */
@Component
public class FullUriFunction implements Function {

    @Autowired
    private ServletContext servletContext;

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("uri");
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate pebbleTemplate, EvaluationContext evaluationContext, int i) {
        String input = (String)args.get("uri");
        StringBuilder fullUri = new StringBuilder(input);
        fullUri.insert(0, servletContext.getContextPath());
        return fullUri.toString();
    }
}
