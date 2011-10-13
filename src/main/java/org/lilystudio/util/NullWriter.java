package org.lilystudio.util;

import java.io.Writer;

/**
 * 不进行任何操作的空输出对象类, 用于不希望输出中间结果的区块函数
 * 
 * @version 0.1.4, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class NullWriter extends Writer {

  @Override
  public void close() {
  }

  @Override
  public void flush() {
  }

  @Override
  public void write(char cbuf[], int off, int len) {
  }
}
