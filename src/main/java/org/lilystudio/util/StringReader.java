package org.lilystudio.util;

import java.io.Reader;

/**
 * 框架默认的字符串缓存输入对象, 非线程安全, 避免线程锁定的开销
 * 
 * @see org.lilystudio.smarty4j.Template
 * 
 * @version 0.1.4, 2009/01/10
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class StringReader extends Reader {

  /** 缓存字符串 */
  private String s;

  /** 当前待读入的位置 */
  private int index;

  /** 字符串长度 */
  private int size;

  /**
   * 创建字符串缓存输入
   * 
   * @param s
   *          字符串缓存
   */
  public StringReader(String s) {
    this.s = s;
    size = s.length();
  }

  @Override
  public void close() {
  }

  @Override
  public int read(char[] cbuf, int off, int len) {
    if (index >= size) {
      return -1;
    }
    int start = index;
    len = Math.min(len, size - index);
    index += len;
    s.getChars(start, index, cbuf, off);
    return len;
  }

  @Override
  public int read() {
    if (index >= size) {
      return -1;
    }
    char c = s.charAt(index);
    index++;
    return c;
  }
}
