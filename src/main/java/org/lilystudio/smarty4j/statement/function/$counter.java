package org.lilystudio.smarty4j.statement.function;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.expression.check.TrueCheck;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 计数器。
 * 
 * <pre>
 * name--计数器名称，默认值为default
 * start--计数器初始值，默认值为1
 * skip--每次递增的步长，默认值为1
 * direction--计数器增长的方向，up(向上)/down(向下)/keep(不变)，默认值为up
 * print--每次计数时是否向输出流中输出，默认值为true
 * assign--结果输出的变量名称，如果省略直接输出至输出流中
 *  
 * {counter start=0 skip=2 print=false direction=&quot;keep&quot;}
 * {counter direction=&quot;up&quot;}-{counter}-{counter skip=1}-{counter}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * 0 - 2 - 4 - 5
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $counter extends LineFunction {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STRING, new StringExpression(
          "default"), "name"),
      new ParameterCharacter(ParameterCharacter.INTOBJECT, null, "start"),
      new ParameterCharacter(ParameterCharacter.INTOBJECT, null, "skip"),
      new ParameterCharacter(ParameterCharacter.STRING, new StringExpression(
          "up"), "direction"),
      new ParameterCharacter(ParameterCharacter.BOOLEAN, new TrueCheck(),
          "print"),
      new ParameterCharacter(ParameterCharacter.STRING, null, "assign") };

  /**
   * 计数器对象, 保存计数器的信息
   */
  private class Counter {

    /** 计数器缺省的初始值 */
    private int start = 1;

    /** 计数器缺省的步长 */
    private int skip = 1;

    /** 计数器缺省的增长方向 */
    private int direction;

    /**
     * 获取计数器的当前值, 同时计数器自动累加
     * 
     * @return 计数器的当前值
     */
    private int get() {
      int result = start;

      switch (direction) {
      case 0:
        start += skip;
        break;
      case 1:
        start -= skip;
        break;
      default:
        break;
      }

      return result;
    }
  }

  private int type;
  
  @SuppressWarnings("unchecked")
  @Override
  public void execute(Context context, Writer writer, Object[] values)
      throws IOException {
    Map<String, Object> counters = (Map<String, Object>) context
        .getProperties("counter");
    // 获得计数器
    Counter counter;
    if (counters == null) {
      counters = new HashMap<String, Object>();
      context.setProperties("counter", counters);
      counter = null;
    } else {
      counter = (Counter) counters.get(values[0]);
    }
    if (counter == null) {
      counter = new Counter();
      counters.put((String) values[0], counter);
    }

    // 设置计数器参数
    if (values[1] != null) {
      counter.start = ((Integer) values[1]).intValue();
    }
    if (values[2] != null) {
      counter.skip = ((Integer) values[2]).intValue();
    }
    if (values[3] != null) {
      counter.direction = type;
    }

    if (values[5] != null) {
      context.set((String) values[5], new Integer(counter.get()));
    } else if (((Boolean) values[4]).booleanValue()) {
      writer.write(Integer.toString(counter.get()));
    }
  }

  @Override
  public void syntax(Template template, Object[] words, int wordSize)
      throws ParseException {
    super.syntax(template, words, wordSize);
    if (getParameter(3) != null) {
      String direction = getParameter(3).toString();
      if (direction.equals("up")) {
        type = 0;
      } else if (direction.equals("down")) {
        type = 1;
      } else if (direction.equals("keep")) {
        type = 2;
      } else {
        throw new ParseException("direction必须是: up, down, keep");
      }
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }

  @Override
  public void scan(Template template) {
    super.scan(template);
    if (getParameter(5) != null) {
      template.preventCacheVariable(getParameter(5).toString());
    }
  }
}