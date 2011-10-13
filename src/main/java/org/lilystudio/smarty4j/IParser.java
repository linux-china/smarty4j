package org.lilystudio.smarty4j;

import java.io.Writer;

/**
 * 模板解析器接口，描述模板对象与数据容器合并产生输出的操作，
 * 根据模板源文件动态生成的类需要实现这个接口。
 * 
 * @see org.lilystudio.smarty4j.Template
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public interface IParser {

  /** ASM名称 */
  String NAME = IParser.class.getName().replace('.', '/');

  /**
   * 解析数据容器信息并输出到指定的文本输出流。
   * 
   * @param context
   *          数据源容器
   * @param writer
   *          输出对象
   */
  void merge(Context context, Writer writer);
}