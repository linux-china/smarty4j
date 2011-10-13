package org.lilystudio.smarty4j.statement.function;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.expression.check.TrueCheck;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 轮换器，对一个数组进行轮换取值。
 * 
 * <pre>
 * name--计数器名称，默认值为default
 * values--用于轮换的源数据数组
 * print--每次计数时是否向输出流中输出，默认值为true
 * advance--取值后轮换器是否往后推进，默认值为true
 * delimiter--values的数据分隔符号，默认值为','
 * assign--结果输出的变量名称，如果省略直接输出至输出流中
 * 
 * {cycle values=&quot;#eeeeee,#d0d0d0&quot;}
 * {cycle}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * #eeeeee
 * #d0d0d0
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $cycle extends LineFunction {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STRING, new StringExpression(
          "default"), "name"),
      new ParameterCharacter(ParameterCharacter.STROBJECT, null, "values"),
      new ParameterCharacter(ParameterCharacter.BOOLEAN, new TrueCheck(),
          "print"),
      new ParameterCharacter(ParameterCharacter.BOOLEAN, new TrueCheck(),
          "advance"),
      new ParameterCharacter(ParameterCharacter.STRING, new StringExpression(
          ","), "delimiter"),
      new ParameterCharacter(ParameterCharacter.STRING, null, "assign") };

  /**
   * 轮换器对象, 保存轮换器的信息
   */
  private class Cycle {

    /** 需要轮换的对象数组 */
    private Object[] objs;

    /** 当前的序号 */
    private int index;

    /**
     * 取出当前的对象
     * 
     * @param isAdvance
     *          是否需要滚动序号
     * @return
     */
    private Object get(boolean isAdvance) {
      Object result = objs[index];

      if (isAdvance) {
        index = (index + 1) % objs.length;
      }

      return result;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void execute(Context context, Writer writer, Object[] values)
      throws IOException {
    Map<String, Object> cycles = (Map<String, Object>) context
        .getProperties("cycle");
    // 获得轮换器对象
    Cycle cycle;
    if (cycles == null) {
      cycles = new HashMap<String, Object>();
      context.setProperties("cycle", cycles);
      cycle = null;
    } else {
      cycle = (Cycle) cycles.get(values[0]);
    }
    if (cycle == null) {
      if (values[1] == null) {
        throw new RuntimeException("没有定义初始对象数组");
      }
      cycle = new Cycle();
      cycles.put((String) values[0], cycle);
    }

    // 设置轮换器的参数
    Object array = values[1];
    if (array != null) {
      if (array instanceof Object[]) {
        cycle.objs = (Object[]) array;
      } else if (array instanceof List) {
        cycle.objs = ((List) array).toArray();
      } else if (array instanceof Map) {
        cycle.objs = ((Map) array).values().toArray();
      } else if (array instanceof String) {
        cycle.objs = ((String) array).split((String) values[4]);
      } else {
        cycle.objs = new Object[] { array };
      }
      cycle.index = 0;
    }

    if (values[5] != null) {
      context.set((String) values[5], cycle.get(((Boolean) values[3])
          .booleanValue()));
    } else if (((Boolean) values[2]).booleanValue()) {
      writer.write(cycle.get(((Boolean) values[3]).booleanValue()).toString());
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