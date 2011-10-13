package org.lilystudio.smarty4j.statement.modifier;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 变量如果为空返回第一个参数的值，缺省为空字符串。
 * 
 * <pre>
 * {&quot;Dealers Will Hear Car Talk at Noon.&quot;|default:'no title'}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * Dealers Will Hear Car Talk at Noon.
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $default extends Modifier {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = { new ParameterCharacter(
      ParameterCharacter.OBJECT, new StringExpression("")) };

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    return obj != null ? obj : values[0]; 
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}