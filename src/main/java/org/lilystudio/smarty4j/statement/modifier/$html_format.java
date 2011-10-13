package org.lilystudio.smarty4j.statement.modifier;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 强制html标准格式输出，如果有未完的标签，将自动补齐，如果有未结束的属性，将自动结束。
 * 
 * <pre>
 * {&quot;&lt;html&gt;&lt;body link=&quot;asdf&quot;&gt;&lt;div link='&quot;|html_format}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * &lt;html&gt;&lt;body link=&quot;asdf&quot;&gt;&lt;/body&gt;&lt;/html&gt;
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $html_format extends Modifier {

  /** HTML标签的正则表达式 */
  private static Pattern p = Pattern
      .compile("< *(/?) *([\\w\\:]+)( +[\\x00-\\x3d\\u003f-\\uffff]*| *)>?");

  /** 字符串的正则表达式 */
  private static Pattern tag = Pattern
      .compile("\"(\\\\.|[\\x00-\\x21\\x23-\\x5b\\u005d-\\uffff])*\"|'(\\\\.|[\\x00-\\x26\\x28-\\x5b\\u005d-\\uffff])*'");

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    String source = obj.toString();
    if (source.length() == 0) {
      return source;
    }
    if (source.charAt(source.length() - 1) == 65533) {
      source = source.substring(0, source.length() - 1);
    }
    List<String> tags = new ArrayList<String>();
    StringBuilder buf = new StringBuilder(256);
    Matcher m = p.matcher(source);

    int start = 0;

    while (m.find()) {
      buf.append(source.substring(start, m.start()));
      start = m.end();
      String tagName = m.group(2);
      if (source.charAt(start - 1) == '>') {
        if (m.group(1).length() == 0) {
          buf.append(tag_format(m.group()));
          // 不需要设置结束标签的标签
          if (!tagName.equalsIgnoreCase("input")
              && !tagName.equalsIgnoreCase("img")
              && !tagName.equalsIgnoreCase("br")) {
            tags.add(tagName);
          }
        } else {
          while (true) {
            if (tags.size() == 0) {
              break;
            }
            // 有开始标签没有结束标签的, 在这里补齐
            String tag = tags.remove(tags.size() - 1);
            buf.append("</").append(tag).append('>');
            if (tag.equalsIgnoreCase(tagName)) {
              break;
            }
          }
        }
      } else {
        // buffer.append(tag_format(m.group()));
        // if (start == source.length()) {
        // buffer.append('>');
        // tags.add(tagName);
        // }
        break;
      }
    }

    buf.append(source.substring(start));

    for (int i = tags.size() - 1; i >= 0; i--) {
      buf.append("</").append(tags.get(i)).append('>');
    }
    return buf.toString();
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }

  /**
   * html标签格式化。
   * 
   * @param source
   *          原始的html标签字符串
   * @return 格式化后的标准html标签字符串
   */
  private String tag_format(String source) {
    Matcher m = tag.matcher(source);
    int start = 0;
    while (m.find()) {
      start = m.end();
    }
    int i1 = source.indexOf('"', start);
    int i2 = source.indexOf('\'', start);
    if (i1 > i2) {
      if (i2 > 0) {
        return source + '"' + '\'';
      } else {
        return source + '"';
      }
    } else if (i2 > i1) {
      if (i1 > 0) {
        return source + '\'' + '"';
      } else {
        return source + '\'';
      }
    }
    return source;
  }
}