package org.lilystudio.smarty4j.statement;

import static org.objectweb.asm.Opcodes.*;

import java.io.Writer;
import java.util.Map;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.util.NullWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * 自定义区块函数，区块函数指的是函数内部包含其它函数或文本，所以必须拥有结束标签的函数，
 * 区块函数在编译过程中将会在解析内部函数或文本之前调用start方法，
 * 在解析完内部函数或文本之后将调用end方法，在模板分析过程中，系统首先初始化函数节点，
 * 然后解析函数的参数，然后设置函数的父函数，最后解析函数的内部数据。
 * 如果不希望进行jvm字节码开发，开发人员应该继承自这个类来实现自己的区块函数扩展节点。
 * 
 * @see org.lilystudio.smarty4j.NullWriter
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public abstract class BlockFunction extends Block {

  /** ASM名称 */
  public static final String NAME = BlockFunction.class.getName()
      .replace('.', '/');

  /** 空输出 */
  protected static final Writer NULL = new NullWriter();

  /** 函数在模板中的编号 */
  private int index;

  /**
   * 函数区块之前需要执行的函数
   * 
   * @param context
   *          数据源容器
   * @param writer
   *          文本输出流
   * @param values
   *          函数参数值
   * @return 区块使用的文本输出流
   * @throws Exception
   *           执行过程中的异常
   */
  public abstract Writer start(Context context, Writer writer, Object[] values)
      throws Exception;

  /**
   * 函数区块之后需要执行的函数
   * 
   * @param context
   *          数据源容器
   * @param writer
   *          文本输出流
   * @param values
   *          函数参数
   * @param childWriter
   *          区块使用的文本输出流
   * @throws Exception
   *           执行过程中的异常
   */
  public abstract void end(Context context, Writer writer, Object[] values,
      Writer childWriter) throws Exception;

  @Override
  public void init(Template template, String name) {
    super.init(template, name);
    this.index = template.addNode(this);
  }

  @Override
  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    mw.visitVarInsn(ALOAD, WRITER);

    parseFunction(mw, local, index);
    mw.visitVarInsn(ALOAD, CONTEXT);
    mw.visitInsn(DUP2);
    mw.visitVarInsn(ALOAD, WRITER);
    parseAllParameters(mw, local, variableNames);
    mw.visitInsn(DUP2_X2);

    mw.visitMethodInsn(INVOKEVIRTUAL, NAME, "start", "(L" + Context.NAME
        + ";Ljava/io/Writer;[Ljava/lang/Object;)Ljava/io/Writer;");

    mw.visitVarInsn(ASTORE, WRITER);

    super.parse(mw, local, variableNames);

    mw.visitVarInsn(ALOAD, WRITER);
    mw.visitMethodInsn(INVOKEVIRTUAL, NAME, "end", "(L" + Context.NAME
        + ";Ljava/io/Writer;[Ljava/lang/Object;Ljava/io/Writer;)V");

    mw.visitVarInsn(ASTORE, WRITER);
  }
}