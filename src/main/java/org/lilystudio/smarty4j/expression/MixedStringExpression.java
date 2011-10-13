package org.lilystudio.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.Utilities;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 混合字符串表达式节点, 向JVM语句栈内放入字符串
 * 
 * @version 0.1.4, 2009/03/01
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class MixedStringExpression extends ObjectExpression {

  /** 所有需要混合的表达式列表 */
  private List<IExpression> expressions = new ArrayList<IExpression>();

  /**
   * 向混合字符串表达式中增加一个新的字符串
   * 
   * @param expression
   *          需要一起显示的表达式
   */
  public void add(String text) {
    expressions.add(new StringExpression(text));
  }

  /**
   * 向混合字符串表达式中增加一个新的表达式
   * 
   * @param expression
   *          需要一起显示的表达式
   */
  public void add(IExpression expression) {
    expressions.add(expression);
  }

  public void setCheckLabel(Label trueLabel, Label falseLabel) {
  }

  public void scan(Template template) {
    super.scan(template);
    for (IExpression expression : expressions) {
      expression.scan(template);
    }
  }

  public void parseSelf(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    mw.visitTypeInsn(NEW, "java/lang/StringBuilder");
    mw.visitInsn(DUP);
    Utilities.visitILdcInsn(mw, 32);
    mw.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>",
        "(I)V");
    for (IExpression exp : expressions) {
      exp.parseString(mw, local, variableNames);
      mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
          "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
    }
    mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString",
        "()Ljava/lang/String;");
  }
}
