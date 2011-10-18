package org.lilystudio.smarty4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 模板引擎对象，提供获取模板对象的操作接口。<br>
 * <br>
 * <b>配置文件smarty.properties示例:</b>
 * <p/>
 * <pre>
 * debug=true - 调试模式
 * encoding=UTF-8 - 模板文件编码集
 * template.path=/ - 模板根路径
 * left.delimiter={ - 左边界定界符
 * right.delimiter=} - 右边界定界符
 * package.function=org.lilystudio.smarty4j.statement.htmlcontrol - 函数扩展包的名称, 以:分隔
 * package.modifier= - 变量调节器扩展包的名称, 以:分隔
 * </pre>
 *
 * @author 欧阳先伟
 * @version 1.0.0, 2010/10/01
 * @see org.lilystudio.smarty4j.Template
 * @since Smarty 1.0
 */
public class Engine {

    /**
     * 模板引擎的包名称
     */
    private static final String PACKAGE_NAME = Engine.class.getPackage()
            .getName();

    /**
     * 引擎的运行状态，当处于调试状态时，模板对象对应的文件更新将自动重新生成模板对象，
     * 模板处理过程中出现错误将有详细的提示信息
     */
    private boolean debug;

    /**
     * 模板源文件根路径
     */
    private String path;

    /**
     * 编码格式，模板源文件的编码格式
     */
    private String encoding;

    /**
     * 左边界定界符，Smarty语句的左边开始字符串
     */
    private String left;

    /**
     * 右边界定界符，Smarty语句的右边结束字符串
     */
    private String right;

    /**
     * 扩展的函数包列表，内置的包为*.statement.function
     */
    private String[] extFunctions;

    /**
     * 扩展的变量调节器包列表，内置的包为*.statement.modifier
     */
    private String[] extModifiers;

    /**
     * 全局配置信息数据集合
     */
    private Map<String, String> configures;

    /**
     * Smarty函数对应的Java类对象的集合
     */
    private Map<String, Class<?>> functions = new HashMap<String, Class<?>>(256);

    /**
     * Smarty函变量调节器对应的Java类对象的集合
     */
    private Map<String, Class<?>> modifiers = new HashMap<String, Class<?>>(256);

    /**
     * 模板对象集合
     */
    private Cache<String, Template> templates = new Cache<String, Template>(1024, -1);


    /**
     * 建立模板引擎，加载默认的配置文件：smarty.properties
     */
    public Engine() {
        this(new HashMap<String, String>());
    }

    /**
     * 建立模板引擎，先加载默认配置，如果设置
     *
     * @param configuration 配置参数
     */
    public Engine(Map<String, String> configuration) {
        Properties prop = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("smarty.properties");
        if (in != null) {
            try {
                prop.load(in);
            } catch (IOException e) {
            }
        }
        for (Map.Entry<String, String> entry : configuration.entrySet()) {
            prop.setProperty(entry.getKey(), entry.getValue());
        }
        if ("true".equals(prop.getProperty("debug"))) {
            debug = true;
        }
        encoding = prop.getProperty("encoding", "UTF-8");
        path = prop.getProperty("template.path", System.getProperty("user.dir"));
        left = prop.getProperty("left.delimiter", "{");
        right = prop.getProperty("right.delimiter", "}");

        StringBuilder s = new StringBuilder(64);
        s.append(PACKAGE_NAME).append(".statement.");
        int len = s.length();
        s.append("function");
        String value = prop.getProperty("package.function");
        if (value != null) {
            s.append(',').append(value);
            extFunctions = s.toString().split("\\s*,\\s*");
        } else {
            extFunctions = new String[]{s.toString()};
        }
        s.setLength(len);
        s.append("modifier");
        value = prop.getProperty("package.modifier");
        if (value != null) {
            s.append(',').append(value);
            extModifiers = (s.toString()).split("\\s*,\\s*");
        } else {
            extModifiers = new String[]{s.toString()};
        }
    }

    /**
     * 如果引擎处于调试状态将返回<tt>true</tt>，否则返回
     * <tt>false</tt>。
     *
     * @return <tt>true</tt>表示引擎处于调试状态
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * 设置引擎是否进入调试状态，在调试状态下，获取模板对象时将检查文件更新情况，
     * 生成的模板对象运行错误时产生的输出将带有调试信息。改变引擎的工作模式，
     * 不影响已经编译完成的模板对象，只影响之后新编译的模板，或者更新后重新编译的模板。
     *
     * @param isDebug <tt>true</tt>表示引擎进入调试状态;
     *                <tt>false</tt>表示引擎进入发布状态
     */
    public void setDebug(boolean isDebug) {
        this.debug = isDebug;
    }

    /**
     * 获取模板文件编码集。
     *
     * @return 编码集名称
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * 设置模板文件编码集。
     *
     * @param encoding 编码集名称
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * 获取引擎中模板文件的根目录位置，在生成模板的过程中，模板文件名相对于根目录位置进行解析。
     *
     * @return 模板文件根目录
     */
    public String getTemplatePath() {
        return path;
    }

    /**
     * 设置引擎中模板文件的根目录位置，在生成模板的过程中，模板文件名相对于根目录位置进行解析。
     *
     * @param path 根目录路径
     */
    public void setTemplatePath(String path) {
        this.path = path;
    }

    /**
     * 获取引擎中语法左边界定界符。
     *
     * @return 模板文件根目录
     */
    public String getLeftDelimiter() {
        return left;
    }

    /**
     * 设置引擎中语法左边界定界符。
     *
     * @param leftDelimiter 左边界定界符
     */
    public void setLeftDelimiter(String leftDelimiter) {
        this.left = leftDelimiter;
    }

    /**
     * 获取引擎中语法右边界定界符。
     *
     * @return 模板文件根目录
     */
    public String getRightDelimiter() {
        return right;
    }

    /**
     * 设置引擎中语法右边界定界符。
     *
     * @param rightDelimiter 右边界定界符
     */
    public void setRightDelimiter(String rightDelimiter) {
        this.right = rightDelimiter;
    }

    /**
     * 添加系统全局配置信息的内容，每次生成数据容器时，这个配置都会复制到数据容器的配置信息中。
     *
     * @param key   系统配置信息名称
     * @param value 系统配置信息数据
     */
    public void addConfig(String key, String value) {
        if (configures == null) {
            configures = new HashMap<String, String>();
        }
        configures.put(key, value);
    }

    /**
     * 建立指定的函数或变量调节器节点实例，在建立时，
     * 系统默认在按函数或变量调节器的包路径列表，依次查找函数或变量调节器类。
     *
     * @param name       函数名称
     * @param isFunction 需要取得的节点类型，<tt>true</tt>表示函数节点，
     *                   <tt>false</tt> 表示变量调节器节点
     * @return 名称对应的函数或变量调节器节点实例
     * @throws Exception 找不到指定名称的函数或变量调节器节点，
     *                   或者函数或变量调节器节点实例化对象失败
     * @see org.lilystudio.smarty4j.statement.IFunction
     */
    public Object createNode(String name, boolean isFunction)
            throws ParseException {
        Map<String, Class<?>> classes = isFunction ? functions : modifiers;
        Class<?> c = classes.get(name);
        if (c == null) {
            // 函数节点类未被初始化, 遍历函数对象包进行查找
            check:
            while (true) {
                String[] packages = isFunction ? extFunctions : extModifiers;
                for (String packageName : packages) {
                    try {
                        c = (Class<?>) Class.forName(packageName + ".$" + name);
                        // 找到指定的节点对象, 退出循环
                        classes.put(name, c);
                        break check;
                    } catch (ClassNotFoundException e) {
                    }
                }
                throw new ParseException((isFunction ? "函数(" : "变量调节器(") + name
                        + ")不存在");
            }
        }
        try {
            return c.newInstance();
        } catch (Exception e) {
            // 出现这种异常的概率极低
            throw new RuntimeException(isFunction ? "函数节点无法实例化" : "变量调节器节点无法实例化");
        }
    }

    /**
     * 获取模板对象，如果模板对象在引擎中不存在，将初始化对应的文件，将它生成模板对象，
     * 如果模板关联的文件发生过更新，并且引擎处于调试模式，将重新生成模板对象。
     *
     * @param name 模板文件名(相对地址)
     * @return 模板对象
     * @throws IOException       如果模板解析过程中有语法错误
     * @throws TemplateException 如果模板有语法错误
     */
    public Template getTemplate(String name) throws IOException, TemplateException {
        name = path + name;
        Template template = templates.get(name);
        if (template != null && !(debug && template.isUpdated())) {
            return template;
        }

        // 模板对象不存在, 或者模板文件被更新过, 重新编译模板对象
        File file = new File(name);
        template = new Template(this, file);
        templates.put(name, template);
        return template;
    }

    /**
     * 根据smarty的文本生成模板
     *
     * @param smartyCode smarty code
     * @return 模板对象
     * @throws IOException       如果模板解析过程中有语法错误
     * @throws TemplateException 如果模板有语法错误
     */
    public Template parseTemplate(String smartyCode) throws IOException, TemplateException {
        Template template = templates.get(smartyCode);
        if (template != null) {
            return template;
        }
        template = new Template(this, smartyCode);
        templates.put(smartyCode, template);
        return template;
    }

    /**
     * 获取系统配置信息集合。
     *
     * @return 系统配置信息集合
     */
    Map<String, String> getConfigures() {
        return configures;
    }
}