package org.lilystudio.smarty4j.statement.modifier;

import java.util.List;
import java.util.regex.Pattern;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.Utilities;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 字符串替换，第一个参数为源字符串，第二个参数为目的字符串。
 * 
 * <pre>
 * {&quot;Child's Stool Great for Use in Garden.$&quot;|replace:' ':'   '|replace:'$':'$1'}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * Child's   Stool   Great   for   Use   in   Garden.$1
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $replace extends Modifier {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STRING, null),
      new ParameterCharacter(ParameterCharacter.STRING, null) };

  /** 正则表达式中需要转义的描述正则表达式 */
  private static Pattern p = Pattern
      .compile("(\\\\|\\{|\\}|\\[|\\]|\\(|\\)|\\*|\\+|\\?|\\^|\\$|\\|)");

  /** 正则转换的匹配规则 */
  private Pattern rule;

  /** 正则转换的替换字符串 */
  private String replacement;

  @Override
  public void init(Template parent, boolean ransack, List<IExpression> values)
      throws ParseException {
    super.init(parent, ransack, values);
    if ((getParameter(0) == null) || (getParameter(1) == null)) {
      throw new ParseException("replace参数错误");
    }
    rule = Pattern.compile(p.matcher(getParameter(0).toString()).replaceAll(
        "\\\\$1"));
    replacement = Utilities.escapeReg(getParameter(1).toString());
  }

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    return rule.matcher(obj.toString()).replaceAll(replacement);
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}