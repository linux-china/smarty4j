package org.lilystudio.smarty4j.statement.modifier;

import java.util.Random;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 产生一个不大于被调节的变量的随机整数。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $random extends Modifier {

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    return Math.abs(new Random(System.currentTimeMillis()).nextInt())
        % ((Number) obj).intValue();
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }
}