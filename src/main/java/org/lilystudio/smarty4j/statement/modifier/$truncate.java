package org.lilystudio.smarty4j.statement.modifier;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.expression.check.FalseCheck;
import org.lilystudio.smarty4j.expression.check.TrueCheck;
import org.lilystudio.smarty4j.expression.number.ConstInteger;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 截取字符串，第一个参数表示字符串的最大长度，缺省为80，
 * 第二个参数表示过长的字符串使用的尾缀，缺省为"..."，第三个参数表示是否检查单词边界，
 * 如果为是将截取整个单词，缺省为否，第四个参数表示鼠标移动到尾缀上时，是否显示整个字符串，
 * 缺省为显示。
 * 
 * <pre>
 * {&quot;Two Sisters Reunite after Eighteen Years at Checkout Counter.&quot;|truncate:30:'...':true}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * Two Sisters Reunite after E...
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $truncate extends Modifier {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.INTEGER, new ConstInteger(80)),
      new ParameterCharacter(ParameterCharacter.STRING, new StringExpression(
          "...")),
      new ParameterCharacter(ParameterCharacter.BOOLEAN, new FalseCheck()),
      new ParameterCharacter(ParameterCharacter.BOOLEAN, new TrueCheck()) };

  /** 单词的正则表达式 */
  private static Pattern p = Pattern.compile(" *\\p{Alpha}+");

  /** HTML需要转义的字符序列 */
  private static Map<String, Boolean> escape = new HashMap<String, Boolean>();

  static {
    escape.put("lt", true);
    escape.put("gt", true);
    escape.put("amp", true);
    escape.put("quot", true);
    escape.put("reg", true);
    escape.put("copy", true);
    escape.put("trade", true);
    escape.put("ensp", true);
    escape.put("emsp", true);
    escape.put("nbsp", true);
  }

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    String text = obj.toString();
    int len = ((Integer) values[0]).intValue();
    int total = 0;

    // 分别处理ASCII字符和非ASCII字符, 认为非ASCII字符是两个字节宽度,
    // ASCII字符是一个字节
    for (int i = text.length() - 1; i >= 0; i--) {
      if (text.charAt(i) > 127) {
        total += 2;
      } else {
        if (text.charAt(i) == ';') {
          int pos = text.lastIndexOf('&', i);
          if (pos > 0 && escape.get(text.substring(pos + 1, i)) != null) {
            i = pos;
          }
        }
        total++;
      }
    }
    // 长度没有超标
    if (len >= total) {
      return text;
    }
    int size = ((String) values[1]).length();
    if (size > len) {
      return values[1];
    }
    // 处理按单词截断
    int index = len - size;
    if (((Boolean) values[2]).booleanValue()) {
      Matcher m = p.matcher(text);
      while (m.find()) {
        if (m.end() >= index) {
          index = m.start() + size - 1;
          break;
        }
      }
    }
    for (int i = text.length() - 1; i >= 0; i--) {
      if (text.charAt(i) > 127) {
        total -= 2;
      } else {
        if (text.charAt(i) == ';') {
          int pos = text.lastIndexOf('&', i);
          if (pos > 0 && escape.get(text.substring(pos + 1, i)) != null) {
            i = pos;
          }
        }
        total--;
      }
      if (total <= index) {
        index = i;
        break;
      }
    }

    // 加上尾缀
    if ((Boolean) values[3]) {
      return text.substring(0, index) + "<span title=\""
          + text.replaceAll("</?[^>]+>", "") + "\">" + values[1] + "</span>";
    } else {
      return text.substring(0, index) + values[1];
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}