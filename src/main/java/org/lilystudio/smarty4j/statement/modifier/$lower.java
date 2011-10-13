package org.lilystudio.smarty4j.statement.modifier;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 字符串转成小写。
 * 
 * <pre>
 * { &quot;Two Convicts Evade Noose, Jury Hung.&quot; | lower }
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * two convicts evade noose, jury hung.
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $lower extends Modifier {

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    return obj.toString().toLowerCase(); 
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }
}