package org.lilystudio.smarty4j.statement.modifier;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 字符串转成大写。
 * 
 * <pre>
 * { &quot;If Strike isn't Settled Quickly it may Last a While.&quot; | upper }
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * IF STRIKE ISN'T SETTLED QUICKLY IT MAY LAST A WHILE.
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $upper extends Modifier {

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    return obj.toString().toUpperCase(); 
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }
}