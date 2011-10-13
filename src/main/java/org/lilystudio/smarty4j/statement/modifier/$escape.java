package org.lilystudio.smarty4j.statement.modifier;

import java.util.List;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 字符串转义，支持html,htmlall,url,quotes,hex,hexentity,
 * javascript等几种类型的转义，第一个参数用于设定需要转义的类型。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $escape extends Modifier {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = { new ParameterCharacter(
      ParameterCharacter.STRING, new StringExpression("html")) };

  /** 需要转义的类型 */
  private int type;

  @Override
  public void init(Template parent, boolean ransack, List<IExpression> values)
      throws ParseException {
    super.init(parent, ransack, values);
    String value = getParameter(0).toString();
    if (value.equals("htmlall")) {
      type = 0;
    } else if (value.equals("html")) {
      type = 1;
    } else if (value.equals("url")) {
      type = 2;
    } else if (value.equals("quotes") || value.equals("javascript")) {
      type = 3;
    } else if (value.equals("hex")) {
      type = 4;
    } else if (value.equals("hexentity")) {
      type = 5;
    } else {
      throw new ParseException("不支持的参数");
    }
  }

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    String s = obj.toString();
    int len = s.length();
    StringBuffer buf = new StringBuffer(256);

    for (int i = 0; i < len; i++) {
      char c = s.charAt(i);
      switch (type) {
      case 0:
        if (c == ' ') {
          buf.append("&#32;");
          continue;
        }
      case 1:
        if (c == '&') {
          buf.append("&#38;");
          continue;
        } else if (c == '"') {
          buf.append("&#34;");
          continue;
        } else if (c == '\'') {
          buf.append("&#39;");
          continue;
        } else if (c == '<') {
          buf.append("&#60;");
          continue;
        } else if (c == '>') {
          buf.append("&#62;");
          continue;
        }
        break;
      case 2:
        if (c == '+') {
          buf.append("%2B");
          continue;
        } else if (c == ' ') {
          c = '+';
        } else if (c == '/') {
          buf.append("%2F");
          continue;
        } else if (c == '?') {
          buf.append("%3F");
          continue;
        } else if (c == '%') {
          buf.append("%25");
          continue;
        } else if (c == '#') {
          buf.append("%23");
          continue;
        } else if (c == '&') {
          buf.append("%26");
          continue;
        } else if (c == '=') {
          buf.append("%3D");
          continue;
        }
        break;
      case 3:
        if (c == '"' || c == '\'' || c == '\\') {
          buf.append('\\');
        }
        break;
      case 4:
        if (c < 256) {
          buf.append('%');
          buf.append(String.format("%x", c));
          continue;
        }
        break;
      case 5:
        if (c < 256) {
          buf.append("&#x");
          buf.append(String.format("%X", c));
          buf.append(';');
        } else {
          buf.append("&#u");
          buf.append(String.format("%X", c));
          buf.append(';');
        }
        continue;
      }
      buf.append(c);
    }
    
    return buf.toString();
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}