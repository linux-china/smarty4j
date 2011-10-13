package org.lilystudio.util;

import java.io.Writer;

/**
 * 框架默认的字符串缓存输出对象, 使用java.lang.StringBuilder,
 * 避免线程锁定的开销
 * 
 * @see org.lilystudio.smarty4j.Template
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class StringWriter extends Writer {

  /** ASM名称 */
  public static final String NAME = StringWriter.class.getName().replace('.',
      '/');

  /** 字符串缓冲区对象 */
  private StringBuilder buf = new StringBuilder(64);

  @Override
  public void write(int c) {
    buf.append((char) c);
  }

  @Override
  public void write(char cbuf[], int off, int len) {
    buf.append(cbuf, off, len);
  }

  @Override
  public void flush() {
  }

  @Override
  public void close() {
  }

  @Override
  public String toString() {
    return buf.toString();
  }
}
