package org.lilystudio.smarty4j.statement.modifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 统计段落数。
 * 
 * <pre>
 * { &quot;War Dims Hope for Peace. Child's Death Ruins Couple's Holiday.\n\nMan is Fatally Slain. Death Causes Loneliness, Feeling of Isolation.&quot;
 *     | count_paragraphs }
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
public class $count_paragraphs extends Modifier {

  /** 段落的正则表达式 */
  private static Pattern p = Pattern
      .compile(" *[\\x00-\\x09\\x0b-\\x1f\\u0021-\\uffff]+(\\x0a|\\z)");

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