package org.lilystudio.smarty4j.statement.modifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 统计句子数。
 * 
 * <pre>
 * { &quot;Two Soviet Ships Collide - One Dies. Enraged Cow Injures Farmer with Axe.&quot;
 *     | count_sentences }
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * 2
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $count_sentences extends Modifier {

  /** 句子的正则表达式 */
  private static Pattern p = Pattern
      .compile(" *[\\x00-\\x1f\\x21-\\x2d\\u002f-\\uffff]+\\.");

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    Matcher m = p.matcher(obj.toString());
    int i = 0;
    while (m.find()) {
      i++;
    }
    return Integer.valueOf(i);
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }
}