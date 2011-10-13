package org.lilystudio.smarty4j;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;

/**
 * smarty template test
 *
 * @author leijuan
 */
public class SmartyTemplateTest extends TestCase {
    /**
     * smarty engine
     */
    private Engine smartyEngine = new Engine();

    /**
     * test to render smarty template
     *
     * @throws Exception exception
     */
    public void testRender() throws Exception {
        Template template = smartyEngine.parseTemplate("<title>{$title}</title>");
        Context context = new Context();
        context.set("title", "欢迎光临");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        template.merge(context, out);
        System.out.println(out.toString("utf-8"));
    }
}
