package org.lilystudio.smarty4j;

import java.util.List;

/**
 * Smarty模板操作异常。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class TemplateException extends Exception {

  private static final long serialVersionUID = 1L;

  /** 模板解析信息列表 */
  private List<ParseMessage> messages;

  /**
   * 建立一个模板操作异常。
   * 
   * @param message
   *          异常提示信息
   */
  public TemplateException(String message) {
    super(message);
  }

  /**
   * 建立一个模板操作异常。
   * 
   * @param message
   *          异常提示信息
   * @param list
   *          解析信息列表
   */
  public TemplateException(String message, List<ParseMessage> parseMessages) {
    super(message);
    messages = parseMessages;
  }

  /**
   * 获取模板解析信息列表。
   * 
   * @return 解析信息列表
   */
  public List<ParseMessage> getParseMessages() {
    return messages;
  }

  @Override
  public String getMessage() {
    StringBuilder s = new StringBuilder(256);
    s.append(super.getMessage());
    if (messages != null) {
      for (ParseMessage msg : messages) {
        s.append('\n');
        switch (msg.getLevel()) {
        case ParseMessage.NORMAL:
          s.append("提示");
          break;
        case ParseMessage.WARNNING:
          s.append("警告");
          break;
        default:
          s.append("错误");
        }
        int lineNumber = msg.getLineNumber();
        if (lineNumber > 0) {
          s.append("(在第").append(lineNumber).append("行)");
        }
        s.append(':').append(msg.getMessage());
      }
    }
    return s.toString();
  }
}