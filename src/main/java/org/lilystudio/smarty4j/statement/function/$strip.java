package org.lilystudio.smarty4j.statement.function;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.TemplateReader;
import org.lilystudio.smarty4j.statement.Block;
import org.lilystudio.smarty4j.statement.IStatement;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.lilystudio.smarty4j.statement.TextStatement;

/**
 * 删除空白字符。
 * 
 * <pre>
 * {strip}
 * &lt;table border=0&gt;
 *   &lt;tr&gt;
 *     &lt;td&gt;
 *       &lt;a href=&quot;http://www.domain.com&quot;&gt;
 *         &lt;font color=&quot;red&quot;&gt;This is a test&lt;/font&gt;
 *       &lt;/a&gt;
 *     &lt;/td&gt;
 *   &lt;/tr&gt;
 * &lt;/table&gt;
 * {/strip}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * &lt;table border=0&gt;&lt;tr&gt;&lt;td&gt;&lt;a href=&quot;http://my.domain.com&quot;&gt;&lt;font color=&quot;red&quot;&gt;This is a test&lt;/font&gt;&lt;/a&gt;&lt;/td&gt;&lt;/tr&gt;&lt;/table&gt;
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $strip extends Block {

  @Override
  public void process(Template template, TemplateReader in, String left,
      String right) {
    super.process(template, in, left, right);

    int size = children.size();
    StringBuilder line = new StringBuilder(4096);
    StringBuilder name = new StringBuilder();
    // 0表示完全去除空格状态,例如字符串的左端
    // 1表示合并文本空格状态,只要没有遇到<符号,多空格就合并成单空格
    // 2表示标签内取标签名状态
    // 3表示标签内合并文本空格状态,只要没有遇到"'=/>符号,多空格就合并成单空格
    // 8表示处理特殊标签
    // 9表示处理style,script,textarea标签中的结束标签判断 操作
    // (int)'"',(int)'\''表示字符串读取状态
    int status = 0;
    // 1表示处理pre标签
    // 2表示处理style标签
    // 3表示处理script标签
    // 4表示处理textarea标签
    int type = 0;
    for (int index = 0; index < size; index++) {
      boolean lastIsWhitespace = false;
      IStatement statement = children.get(index);
      if (!(statement instanceof TextStatement)) {
        continue;
      }
      String text = ((TextStatement) statement).getText();
      line.setLength(0);
      int end = text.length();
      for (int start = 0; start < end; start++) {
        char c = text.charAt(start);
        switch (status) {
        case 0:
        case 1:
          if (Character.isWhitespace(c)) {
            lastIsWhitespace = true;
            continue;
          }
          if (c == '<') {
            name.setLength(0);
            if (type == 2) {
              status = 9;
            } else {
              status = 2;
            }
          } else if (status == 0) {
            status = 1;
          } else {
            if (type != 2) {
              if (lastIsWhitespace) {
                line.append(' ');
              }
            } else {
              int lastIndex = line.length() - 1;
              char lastChar = line.charAt(lastIndex);
              if (c == '}') {
                if (lastChar == ';') {
                  line.setCharAt(lastIndex, c);
                  continue;
                }
              } else if (c == ';') {
                if (lastChar == '{' || lastChar == ';') {
                  continue;
                }
              } else if (lastIsWhitespace && c != ':' && c != '{' && c != '('
                  && c != ')' && c != ',' && lastChar != ';' && lastChar != ':'
                  && lastChar != '{' && lastChar != '}' && lastChar != '#'
                  && lastChar != '.' && lastChar != '(' && lastChar != ')'
                  && lastChar != ',') {
                line.append(' ');
              }
            }
          }
          lastIsWhitespace = false;
          break;
        case 2:
        case 3:
          if (Character.isWhitespace(c)) {
            lastIsWhitespace = true;
            if (status == 2) {
              if (name.length() > 0
                  && (name.length() > 1 || name.charAt(0) != '/')) {
                status = 3;
              }
            }
            continue;
          } else if (c == '>') {
            String tag = name.toString();
            if (type == 1) {
              if (tag.equals("/pre")) {
                status = 0;
                type = 0;
              } else {
                status = 8;
              }
            } else if (tag.equals("pre")) {
              status = 8;
              type = 1;
            } else if (tag.equals("style")) {
              status = 0;
              type = 2;
            } else if (tag.equals("script")) {
              status = 8;
              type = 3;
            } else if (tag.equals("textarea")) {
              status = 8;
              type = 4;
            } else {
              status = 0;
            }
          } else if (status == 2) {
            name.append(Character.toLowerCase(c));
          } else if (c == '"' || c == '\'') {
            status = c;
          } else if (c != '=' && lastIsWhitespace) {
            line.append(' ');
          }
          lastIsWhitespace = false;
          break;
        case 8:
          switch (type) {
          case 1:
            if (c == '<') {
              name.setLength(0);
              status = 2;
            }
            break;
          case 2:
            break;
          case 3:
            if (c == '<') {
              name.setLength(0);
              status = 9;
            } else if (c == '"' || c == '\'') {
              status = c;
            }
            break;
          case 4:
            if (c == '<') {
              name.setLength(0);
              status = 9;
            }
            break;
          }
          break;
        case 9:
          if (Character.isWhitespace(c)) {
            lastIsWhitespace = true;
          } else if (c == '<') {
            name.setLength(0);
          } else if (c == '>') {
            String tag = name.toString();
            if (tag.equals("/textarea")) {
              status = 0;
              type = 0;
              line.setLength(line.lastIndexOf("<"));
              line.append('<').append(tag);
            } else {
              status = 8;
            }
          } else {
            if (!lastIsWhitespace || name.length() == 0
                || (name.length() == 1 && name.charAt(0) == '/')) {
              name.append(Character.toLowerCase(c));
              lastIsWhitespace = false;
            }
          }
          break;
        case '"':
        case '\'':
          if (status == c) {
            status = 3;
          }
          break;
        }
        line.append(c);
      }
      children.set(index, new TextStatement(line.toString()));
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }
}