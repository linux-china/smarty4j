package org.lilystudio.smarty4j.statement.modifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 单词首字母大写。
 * 
 * <pre>
 * { &quot;Police begin campaign to rundown jaywalkers.&quot; | capitalize }
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * Police Begin Campaign To Rundown Jaywalkers.
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $capitalize extends Modifier {

  /** 单词的正则表达式 */
  private static final Pattern p = Pattern
      .compile("\\p{Alpha}\\w*([\\s\\p{Punct}]|$)");

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    StringBuilder buf = new StringBuilder(obj.toString());
    Matcher m = p.matcher(buf);
    while (m.find()) {
      int start = m.start();
      buf.setCharAt(start, Character.toUpperCase(buf.charAt(start)));
    }
    return buf.toString();
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }
}