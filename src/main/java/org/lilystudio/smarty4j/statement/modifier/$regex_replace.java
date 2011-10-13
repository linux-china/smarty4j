package org.lilystudio.smarty4j.statement.modifier;

import java.util.List;
import java.util.regex.Pattern;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 正则表达式替换，第一个参数为正则表达式，第二个参数为结果字符串。
 * 
 * <pre>
 * {&quot;Infertility unlikely to\nbe passed on, experts say.&quot;|regex_replace:'[\\r\\t\\n]':'$1'}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * Infertility unlikely to$1be passed on, experts say.
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $regex_replace extends Modifier {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STRING, null),
      new ParameterCharacter(ParameterCharacter.STRING, null) };

  /** 正则转换的匹配规则 */
  private Pattern rule;

  @Override
  public void init(Template parent, boolean ransack, List<IExpression> values)
      throws ParseException {
    super.init(parent, ransack, values);
    if ((getParameter(0) == null) || (getParameter(1) == null)) {
      throw new ParseException("regex_replace参数错误");
    }
    rule = Pattern.compile(getParameter(0).toString());
  }

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    return rule.matcher(obj.toString()).replaceAll((String) values[1]);
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}