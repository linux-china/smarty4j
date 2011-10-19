package org.lilystudio.smarty4j;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * smarty template test
 *
 * @author leijuan
 */
public class SmartyTemplateTest extends TestCase {
    /**
     * smarty engine
     */
    private Engine smartyEngine;

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Map<String, String> config = new HashMap<String, String>();
        config.put("template.path", "src/test/resources/templates/");
        config.put("encoding", "GBK");
        this.smartyEngine = new Engine(config);
    }

    /**
     * test to render smarty template
     *
     * @throws Exception exception
     */
    public void testRender() throws Exception {
        Template template = smartyEngine.getTemplate("demo.tpl");
        Context context = new Context();
        context.set("title", "欢迎光临222");
        List<User> users = new ArrayList<User>();
        users.add(new User(1, "马克"));
        users.add(new User(2, "马克"));
        context.set("users", users);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        template.merge(context, out);
        System.out.println(out.toString("GBK"));
    }
}
