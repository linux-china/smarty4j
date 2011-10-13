package org.lilystudio.smarty4j.statement.modifier;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 统计字符数。
 * 
 * <pre>
 * { &quot;Cold Wave Linked to Temperatures.&quot; | count_characters }
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * 33
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $count_characters extends Modifier {

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    return Integer.valueOf(obj.toString().length());
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }
}