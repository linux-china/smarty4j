package org.lilystudio.smarty4j.statement;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.ObjectExpression;
import org.lilystudio.smarty4j.expression.TranslateObject;
import org.objectweb.asm.MethodVisitor;

/**
 * 表达式输出语句。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class PrintStatement implements IStatement {

  /** 需要输出的对象表达式 */
  private ObjectExpression expression;

  /**
   * 建立变量输出语句。
   * 
   * @param expression
   *          待输出的表达式
   */
  public PrintStatement(IExpression expression) {
    this.expression = new TranslateObject(expression);
  }

  public void scan(Template template) {
    expression.scan(template);
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    mw.visitVarInsn(ALOAD, WRITER);
    expression.parseString(mw, local, variableNames);
    mw.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write",
        "(Ljava/lang/String;)V");
  }
}