package org.lilystudio.smarty4j.statement;

import static org.objectweb.asm.Opcodes.*;

import java.io.Writer;
import java.util.Map;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Template;
import org.objectweb.asm.MethodVisitor;

/**
 * 自定义单行函数节点，单行函数是指不需要结束标签的函数，语句只占用一行，在模板解析过程中，
 * 将调用execute方法，如果不希望进行jvm字节码开发，
 * 开发人员应该继承自这个类来实现自己的行函数扩展节点。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public abstract class LineFunction extends Function {

  /** ASM名称 */
  public static final String NAME = LineFunction.class.getName()
      .replace('.', '/');

  /** 函数在模板中的编号 */
  private int index;

  /**
   * 单行函数执行主体，由于不需要管理区块内的信息，所以没有childWriter参数。
   * 
   * @param context
   *          数据源容器
   * @param writer
   *          文本输出流
   * @param values
   *          函数参数
   * @throws Exception
   *           执行过程中的任何异常
   */
  public abstract void execute(Context context, Writer writer, Object[] values)
      throws Exception;

  @Override
  public void init(Template template, String name) {
    super.init(template, name);
    this.index = template.addNode(this);
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    parseFunction(mw, local, index);
    mw.visitVarInsn(ALOAD, CONTEXT);
    mw.visitVarInsn(ALOAD, WRITER);
    parseAllParameters(mw, local, variableNames);
    mw.visitMethodInsn(INVOKEVIRTUAL, NAME, "execute", "(L" + Context.NAME
        + ";Ljava/io/Writer;[Ljava/lang/Object;)V");
  }
}