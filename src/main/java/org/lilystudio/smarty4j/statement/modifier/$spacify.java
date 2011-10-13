package org.lilystudio.smarty4j.statement.modifier;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 在每个字符间插入一个字符串，第一个参数表达待插入的字符串，缺省为插入一个空格。
 * 
 * <pre>
 * {&quot;Something Went Wrong in Jet Crash, Experts Say.&quot;|spacify:'&circ;&circ;'}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * S&circ;&circ;o&circ;&circ;m&circ;&circ;e&circ;&circ;t&circ;&circ;h&circ;&circ;i&circ;&circ;n&circ;&circ;g&circ;&circ; &circ;&circ;W&circ;&circ;e&circ;&circ;n&circ;&circ;t&circ;&circ; &circ;&circ;W&circ;&circ;r&circ;&circ;o&circ;&circ;n&circ;&circ;g&circ;&circ; &circ;&circ;i&circ;&circ;n&circ;&circ; &circ;&circ;J&circ;&circ;e&circ;&circ;t&circ;&circ; &circ;&circ;C&circ;&circ;r&circ;&circ;a&circ;&circ;s&circ;&circ;h&circ;&circ;,&circ;&circ; &circ;&circ;E&circ;&circ;x&circ;&circ;p&circ;&circ;e&circ;&circ;r&circ;&circ;t&circ;&circ;s&circ;&circ; &circ;&circ;S&circ;&circ;a&circ;&circ;y&circ;&circ;.
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $spacify extends Modifier {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = { new ParameterCharacter(
      ParameterCharacter.STRING, new StringExpression(" ")) };

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    StringBuilder buf = new StringBuilder(64);

    String text = obj.toString();
    buf.append(text.charAt(0));
    for (int i = 1, len = text.length(); i < len; i++) {
      buf.append(values[0]).append(text.charAt(i));
    }

    return buf.toString();
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}