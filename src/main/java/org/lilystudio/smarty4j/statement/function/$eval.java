package org.lilystudio.smarty4j.statement.function;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.lilystudio.util.StringWriter;

/**
 * 按Smarty的语法直接执行语句字符串。
 * 
 * <pre>
 * var--Smarty语句
 * assign--结果输出的变量名称，如果省略直接输出至输出流中
 * 
 * {capture name=&quot;test&quot;}$smarty.capture.test{/capture}
 * {eval var=$smarty.capture.test}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * $smarty.capture.test
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $eval extends LineFunction {

  /** 模板缓存 */
  private Map<String, Template> templates = new HashMap<String, Template>();

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STROBJECT, "var"),
      new ParameterCharacter(ParameterCharacter.STRING, null, "assign") };

  @Override
  public void execute(Context context, Writer writer, Object[] values)
      throws Exception {
    Object assign = values[1];
    if (assign != null) {
      writer = new StringWriter();
    }

    if (templates.size() > 1024) {
      synchronized (this) {
        templates.clear();
      }
    }

    // 生成无名模板用于解析字符串
    String text = (String) values[0];
    Template template = templates.get(text);
    if (template == null) {
      template = new Template(context.getTemplate().getEngine(), text);
      templates.put(text, template);
    }
    template.merge(context, writer);

    if (assign != null) {
      context.set((String) assign, writer.toString());
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }

  @Override
  public void scan(Template template) {
    super.scan(template);
    template.preventAllCache();
  }
}