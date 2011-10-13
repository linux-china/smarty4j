package org.lilystudio.smarty4j.statement.modifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 统计单词数。
 * 
 * <pre>
 * { &quot;Dealers Will Hear Car Talk at Noon.&quot; | count_words }
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * 7
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $count_words extends Modifier {

  /** 单词的正则表达式 */
  private static Pattern p = Pattern.compile("\\p{Alpha}+");

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