package org.lilystudio.smarty4j;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;

/**
 * 节点操作接口，Smarty模板在解析过程中将输入文本构建成一个节点树，最顶层的就是文档节点，
 * 然后通过对节点树左序遍历的方式生成每一个节点的二进制操作码，实现模板解析接口。
 * 
 * @see org.lilystudio.smarty4j.IParser
 * @see org.lilystudio.smarty4j.Template
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public interface INode {

  /** ASM名称 */
  public static final String NAME = INode.class.getName().replace('.', '/');

  /** 解析器对象，使用mw.visitVarInsn(ALOAD, THIS)引用 */
  int THIS = 0;

  /** 数据容器，使用mw.visitVarInsn(ALOAD, CONTEXT)引用 */
  int CONTEXT = 1;

  /** 输出对象，使用mw.visitVarInsn(ALOAD, WRITER)引用 */
  int WRITER = 2;

  /** 模板对象，使用mw.visitVarInsn(ALOAD, TEMPLATE)引用 */
  int TEMPLATE = 3;

  /** JVM语句栈的变量起始位置 */
  int LOCAL_START = 4;

  /**
   * 扫描所有的节点。
   * 
   * @param template
   *          模板对象
   */
  void scan(Template template);

  /**
   * 节点代码转换，根据节点的信息转换成实际的JVM代码。
   * 
   * @param mw
   *          ASM方法访问对象
   * @param local
   *          ASM方法内部的语句栈局部变量起始位置
   * @param variableNames
   *          需要缓存的变量名集合
   */
  void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames);
}