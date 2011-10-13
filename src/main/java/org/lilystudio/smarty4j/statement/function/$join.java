package org.lilystudio.smarty4j.statement.function;

import java.io.IOException;
import java.io.Writer;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 合并一个数组变成一个字符串。
 * 
 * <pre>
 * values--数组无素
 * separator--数组元素之间的分隔符
 * assign--输出的模板数据名，如果设置了assign，变量通过$[assign]访问，name的设置将失效
 * 
 * {assign var=&quot;animals&quot; value=&quot;Rat,Ox,Tiger,Hare,Dragon,Serpent,Horse,Sheep,Monkey,Rooster,Dog,Boar&quot; delimiter=&quot;,&quot;}
 * {join values=$animals separator=&quot;|&quot;}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * Rat | Ox | Tiger | Hare | Dragon | Serpent | Horse | Sheep | Monkey | Rooster
 *     | Dog | Boar
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $join extends LineFunction {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.OBJECT, "values"),
      new ParameterCharacter(ParameterCharacter.STRING, "separator"),
      new ParameterCharacter(ParameterCharacter.STRING, null, "assign") };

  @Override
  public void execute(Context context, Writer writer, Object[] values)
      throws IOException {
    Object[] list = $foreach.getLooper(values[0]);
    StringBuilder s = new StringBuilder();
    for (Object o : list) {
      s.append(o).append(values[1]);
    }
    s.setLength(s.length() - 1);
    if (values[3] != null) {
      context.set((String) values[3], s.toString());
    } else {
      writer.write(s.toString());
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }

  @Override
  public void scan(Template template) {
    super.scan(template);
    if (getParameter(2) != null) {
      template.preventCacheVariable(getParameter(2).toString());
    }
  }
}