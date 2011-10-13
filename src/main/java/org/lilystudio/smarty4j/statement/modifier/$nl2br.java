package org.lilystudio.smarty4j.statement.modifier;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 将字符串中的回车换行转换成&lt;br/&gt;。
 * 
 * <pre>
 * { &quot;Sun or rain expected\ntoday, dark tonight&quot; | nl2br }
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * Sun or rain expected&lt;br /&gt;today, dark tonight
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $nl2br extends Modifier {

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    StringBuilder buf = new StringBuilder(64);
    String text = obj.toString();
    int size = text.length();
    for (int i = 0; i < size; i++) {
      char c = text.charAt(i);
      if (c == '\n') {
        buf.append("<br/>");
      } else if (c != '\r') {
        buf.append(c);
      }
    }
    return buf.toString();
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }
}