package org.lilystudio.smarty4j.statement.function;

import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.lilystudio.util.StringWriter;

/**
 * 模板加载，子模板内使用的变量不会影响到父模板中相应对象的值。
 * 
 * <pre>
 * file--模板文件名，文件名如果以'/'开头表示相对Engine的地址，否则表示当前模板的相对地址
 * assign--结果输出的变量名称，如果省略直接输出至输出流中
 * 此外，还可以在函数中写若干个NAME=VALUE，这些都会做为参数传递至子模板中。
 * </pre>
 * 
 * @see org.lilystudio.smarty4j.Engine#getTemplatePath()
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $include extends LineFunction {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STROBJECT, "file"),
      new ParameterCharacter(ParameterCharacter.STRING, null, "assign") };

  @Override
  public void execute(Context context, Writer writer, Object[] values)
      throws Exception {
    Object assign = values[1];
    if (assign != null) {
      writer = new StringWriter();
    }

    // 加载子模板, 设置子模板的父容器
    Template template = context.getTemplate();
    String name = template.getPath((String) values[0], true);

    template = template.getEngine().getTemplate(name);
    Context childContext = new Context(context);

    int len = values.length;
    for (int i = 2; i < len; i += 2) {
      childContext.set((String) values[i], values[i + 1]);
    }

    template.merge(childContext, writer);

    if (assign != null) {
      context.set((String) assign, writer.toString());
    }
  }

  @Override
  public void process(ParameterCharacter[] parameters,
      Map<String, IExpression> fields) throws ParseException {
    super.process(parameters, fields);
    // 移除必须存在的参数
    fields.remove("file");
    fields.remove("assign");

    // 保存所有的参数与值
    IExpression[] expressions = new IExpression[fields.size() * 2 + 2];
    expressions[0] = getParameter(0);
    expressions[1] = getParameter(1);

    int i = 2;
    for (Entry<String, IExpression> entry : fields.entrySet()) {
      expressions[i] = new StringExpression(entry.getKey());
      expressions[i + 1] = entry.getValue();
      i += 2;
    }

    setParameters(expressions);
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }

  @Override
  public void scan(Template template) {
    super.scan(template);
    if (getParameter(1) != null) {
      template.preventCacheVariable(getParameter(1).toString());
    }
  }
}