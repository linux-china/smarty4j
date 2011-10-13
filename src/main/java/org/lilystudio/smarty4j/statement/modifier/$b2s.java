package org.lilystudio.smarty4j.statement.modifier;

import java.io.UnsupportedEncodingException;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 字节数组转换成为字符串，第一个参数表示字节数组的编码方式，缺省是UTF-8。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $b2s extends Modifier {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = { new ParameterCharacter(
      ParameterCharacter.STRING, new StringExpression("UTF-8")) };

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    try {
      return new String((byte[]) obj, (String) values[0]);
    } catch (UnsupportedEncodingException e) {
      return "";
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}