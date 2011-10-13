package org.lilystudio.smarty4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * 模板默认的二进制/文本转换输出类，在模板解析时，如果用户传入的是二进制输出流，
 * 将自动建立这个类，这个类可以方便的在二进制/文本输出之间转换。
 * 
 * @see org.lilystudio.smarty4j.Template
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class TemplateWriter extends BufferedWriter {

  /** ASM名称 */
  public static final String NAME = TemplateWriter.class.getName().replace('.',
      '/');

  /** 二进制输出流 */
  private OutputStream out;

  /**
   * 创建二进制/文本转换输出流对象。
   * 
   * @param out
   *          二进制输出流
   * @param encoding
   *          编码集
   * @throws IOException
   *           构造对象时产生IO错误
   */
  public TemplateWriter(OutputStream out, String encoding) throws IOException {
    super(new OutputStreamWriter(out, encoding));
    this.out = out;
  }

  /**
   * 获取二进制输出流。
   * 
   * @return 与当前文本输出对象关联的二进制输出流
   * @throws IOException
   *           刷新之前提交的信息时产生IO错误
   */
  public OutputStream getOutputStream() throws IOException {
    flush();
    return out;
  }
}
