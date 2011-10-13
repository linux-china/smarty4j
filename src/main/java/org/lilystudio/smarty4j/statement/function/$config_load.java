package org.lilystudio.smarty4j.statement.function;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 加载变量配置文件，文件的数据使用NAME=VALUE的标准格式。
 * 
 * <pre>
 * file--文件名
 * scope--变量作用的范围，global(全局)/parent(父模板)/local(当前模板)，默认值为local
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $config_load extends LineFunction {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STRING, "file"),
      new ParameterCharacter(ParameterCharacter.STRING, new StringExpression(
          "local"), "scope") };

  /** 配置参数加载的类型 */
  private int type;

  @Override
  public void execute(Context context, Writer writer, Object[] values)
      throws IOException {
    Template template = context.getTemplate();
    FileInputStream in = new FileInputStream(template.getPath(
        (String) values[0], true));
    Properties prop = new Properties();
    try {
      prop.load(in);
    } finally {
      in.close();
    }

    // 取得父容器的配置信息
    Engine engine = template.getEngine();
    Map<String, Object> config = context.getConfigures();
    Context parentContext = context.getParent();
    Map<String, Object> parent;
    if (parentContext != null) {
      parent = parentContext.getConfigures();
    } else {
      parent = null;
    }

    // 根据配置加载的级别, 设置配置的内容范围
    for (Enumeration<?> i = prop.propertyNames(); i.hasMoreElements();) {
      String key = i.nextElement().toString();
      String value = prop.getProperty(key);
      config.put(key, value);
      if (type < 2) {
        if (parent != null) {
          parent.put(key, value);
        }
        if (type < 1) {
          engine.addConfig(key, value);
        }
      }
    }
  }

  @Override
  public void syntax(Template template, Object[] words, int wordSize)
      throws ParseException {
    super.syntax(template, words, wordSize);
    String scope = getParameter(1).toString();
    if (scope.equals("local")) {
      type = 2;
    } else if (scope.equals("parent")) {
      type = 1;
    } else if (scope.equals("global")) {
      type = 0;
    } else {
      throw new ParseException("scope必须是: global, parent, local");
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}