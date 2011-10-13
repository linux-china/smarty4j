package org.lilystudio.smarty4j.statement.function;

import java.io.IOException;
import java.io.Writer;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 将一个字符串重复显示若干次。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $repeat extends LineFunction {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STROBJECT, "value"),
      new ParameterCharacter(ParameterCharacter.INTOBJECT, "count") };

  @Override
  public void execute(Context context, Writer writer, Object[] values)
      throws IOException {
    StringBuilder s = new StringBuilder(64);
    String value = (String) values[0];
    int count = (Integer) values[1];
    for (int i = 0; i < count; i++) {
      s.append(value);
    }
    writer.write(s.toString());
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}
