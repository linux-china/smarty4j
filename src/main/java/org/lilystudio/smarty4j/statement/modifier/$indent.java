package org.lilystudio.smarty4j.statement.modifier;

import java.util.List;
import java.util.regex.Pattern;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.expression.number.ConstInteger;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 行缩进，第一个参数指定缩进量的大小，第二个参数指定缩进的单位，默认缩进4个空格。
 * 
 * <pre>
 * { &quot;kick\ntest\n&quot; | indent }
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 *     kick
 *     test
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $indent extends Modifier {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.INTEGER, new ConstInteger(4)),
      new ParameterCharacter(ParameterCharacter.STRING, new StringExpression(
          " ")) };

  /** 换行的正则表达式 */
  private static Pattern p = Pattern.compile("(\\A|\n)");

  /** 需要正则转义的行缩进字符串 */
  private String replacement;

  @Override
  public void init(Template parent, boolean ransack, List<IExpression> values)
      throws ParseException {
    super.init(parent, ransack, values);

    StringBuilder buf = new StringBuilder(64);
    int count = ((ConstInteger) getParameter(0)).getValue();
    String indent = this.getParameter(1).toString();
    for (int i = 0; i < count; i++) {
      buf.append(indent);
    }
    replacement = buf.toString();
  }

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    return p.matcher(obj.toString()).replaceAll("$1" + replacement);
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}