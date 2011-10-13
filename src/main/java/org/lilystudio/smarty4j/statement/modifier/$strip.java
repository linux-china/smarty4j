package org.lilystudio.smarty4j.statement.modifier;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 将相连的空白字符替换成一个字符串，第一个参数为待替换的字符串，缺省为一个空格，
 * 即将所有的空白字符压缩。
 * 
 * <pre>
 * {&quot;Grandmother of\neight makes\t    hole in one.&quot;|strip:' '}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * Grandmother of eight makes hole in one.
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $strip extends Modifier {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = { new ParameterCharacter(
      ParameterCharacter.STRING, new StringExpression(" ")) };

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    StringBuilder buf = new StringBuilder(64);
    String insert = (String) values[0];

    String s = obj.toString();
    int size = s.length();
    boolean lastIsWhitespace = false;
    for (int i = 0; i < size; i++) {
      char c = s.charAt(i);
      if (Character.isWhitespace(c)) {
        lastIsWhitespace = true;
      } else {
        if (lastIsWhitespace) {
          buf.append(insert);
        }
        buf.append(c);
        lastIsWhitespace = false;
      }
    }

    return buf.toString();
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}