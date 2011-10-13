package org.lilystudio.smarty4j;

/**
 * Smarty模板语法异常。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class ParseException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * 建立一个模板语法异常。
   * 
   * @param message
   *          异常提示信息
   */
  public ParseException(String message) {
    super(message);
  }
}