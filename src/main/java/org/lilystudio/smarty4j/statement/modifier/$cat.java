package org.lilystudio.smarty4j.statement.modifier;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 字符串连接，第一个参数为需要连接的字符串，缺省值为空字符串。
 * 
 * <pre>
 * {&quot;Psychics predict world didn't end&quot;|cat:' yesterday.'}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * Psychics predict world didn't end yesterday.
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $cat extends Modifier {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = { new ParameterCharacter(
      ParameterCharacter.STROBJECT, new StringExpression("")) };

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    String s = (String) values[0];
    if (s.length() > 0) {
      return obj.toString() + s;
    } else {
      return obj.toString();
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}