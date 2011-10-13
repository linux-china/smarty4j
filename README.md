# Introduction:

Smarty4J is an template engine implemented by Java to deal with Smarty template. For Smarty please visit http://www.smarty.net

## How to Use

### Open your pom.xml and add:

    <dependency>
         <groupId>org.lilystudio</groupId>
         <artifactId>smarty4j</artifactId>
         <version>1.0.1</version>
    </dependency>

### Example:

        import junit.framework.TestCase;
        import org.lilystudio.smarty4j.Context;
        import org.lilystudio.smarty4j.Engine;
        import org.lilystudio.smarty4j.Template;

        import java.io.ByteArrayOutputStream;

        /**
         * smarty template test
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
                Template template = smartyEngine.getTemplate("/src/test/resources/demo.tpl"); //打开模板文件
                Context context = new Context();
                context.set("title", "雷卷");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                template.merge(context, out);
                System.out.println(out.toString("utf-8"));
            }
        }
