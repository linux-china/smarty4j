package org.lilystudio.smarty4j;

/**
 * 模板解析过程中的信息保存。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class ParseMessage {

  /** 普通的信息 */
  public static final int NORMAL = 0;

  /** 警告信息 */
  public static final int WARNNING = 1;

  /** 错误信息 */
  public static final int ERROR = 2;

  /** 信息等级 */
  private int level;

  /** 信息出现时的行号 */
  private int lineNumber;

  /** 信息出现时的开始位置(相对于整个文档) */
  private int start;

  /** 信息出现时的结束位置(相对于整个文档) */
  private int end;

  /** 信息文本 */
  private String message;

  /**
   * 建立信息数据。
   * 
   * @param level
   *          信息等级
   * @param lineNumber
   *          行号
   * @param start
   *          开始位置
   * @param end
   *          结束位置
   * @param message
   *          文本
   */
  ParseMessage(int level, int lineNumber, int start, int end, String message) {
    this.level = level;
    this.lineNumber = lineNumber;
    this.start = start;
    this.end = end;
    this.message = message;
  }

  /**
   * 获取信息等级。
   * 
   * @return 信息等级
   */
  public int getLevel() {
    return level;
  }

  /**
   * 获取信息出现时的行号。
   * 
   * @return 信息出现时的行号
   */
  public int getLineNumber() {
    return lineNumber;
  }

  /**
   * 获取信息出现时的开始位置。
   * 
   * @return 信息出现时的开始位置
   */
  public int getStart() {
    return start;
  }

  /**
   * 获取信息出现时的结束位置。
   * 
   * @return 信息出现时的结束位置
   */
  public int getEnd() {
    return end;
  }

  /**
   * 获取信息内容文本。
   * 
   * @return 信息内容文本
   */
  public String getMessage() {
    return message;
  }
}