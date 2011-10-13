package org.lilystudio.smarty4j.statement.modifier;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 统计数组或字符串长度，或者集合的元素个数。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $count extends Modifier {

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    if (obj instanceof List) {
      return Integer.valueOf(((List<?>) obj).size());
    } else if (obj.getClass().isArray()) {
      return Array.getLength(obj);
    } else if (obj instanceof Map) {
      return Integer.valueOf(((Map<?, ?>) obj).size());
    } else if (obj instanceof String) {
      return Integer.valueOf(obj.toString().length());
    } else {
      return Integer.valueOf(1);
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }
}