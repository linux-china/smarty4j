package org.lilystudio.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.TemplateWriter;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.VariableExpression;
import org.lilystudio.smarty4j.statement.Function;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.objectweb.asm.MethodVisitor;

/**
 * bytes函数，语法与一般的函数不同，变量名直接跟在bytes后，
 * 用于在混合输出时输出二进制流。如果要使用这个函数，
 * 在merge时必须传入的为OutputStream，而不是Writer，否则将产生运行异常。
 * 
 * <pre>
 * {bytes $DATA}
 * </pre>
 * 
 * @see org.lilystudio.smarty4j.Template#merge(org.lilystudio.smarty4j.Context,
 *      java.io.OutputStream)
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $bytes extends Function {

  /** 需要输出的表达式 */
  private IExpression exp;

  @Override
  public void syntax(Template template, Object[] words, int wordSize)
      throws ParseException {
    Object var = words[3];
    if ((wordSize > 4) || !(var instanceof VariableExpression)) {
      throw new ParseException("参数错误");
    }
    exp = (IExpression) var;
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    // OutputStream out = ((OutputStreamWriter)
    // writer).getOutputStream();
    // out.write((byte[]) exps[0]);
    // out.flush();
    mw.visitVarInsn(ALOAD, WRITER);
    mw.visitTypeInsn(CHECKCAST, TemplateWriter.NAME);
    mw.visitMethodInsn(INVOKEVIRTUAL, TemplateWriter.NAME, "getOutputStream",
        "()Ljava/io/OutputStream;");
    mw.visitInsn(DUP);
    exp.parse(mw, local, variableNames);
    mw.visitTypeInsn(CHECKCAST, "[B");
    mw.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
    mw.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "flush", "()V");

  }
}