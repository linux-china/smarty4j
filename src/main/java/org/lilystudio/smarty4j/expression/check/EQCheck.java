package org.lilystudio.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import org.lilystudio.smarty4j.Utilities;
import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.MethodVisitor;

/**
 * 等于操作布尔表达式节点, 检测两个表达式的结果是否弱类型相等
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class EQCheck extends BinaryCheck {

  /**
   * 创建等于操作布尔表达式节点
   * 
   * @param exp1
   *          表达式1
   * @param exp2
   *          表达式2
   */
  public EQCheck(IExpression exp1, IExpression exp2) {
    super(exp1, exp2);
  }

  @Override
  protected void checkDouble(MethodVisitor mw, boolean swap) {
    mw.visitInsn(DCMPL);
    Utilities.visitSCInsn(mw, IFEQ, trueLabel, falseLabel);
  }

  @Override
  protected void checkString(MethodVisitor mw, boolean swap) {
    mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "compareTo",
        "(Ljava/lang/String;)I");
    Utilities.visitSCInsn(mw, IFEQ, trueLabel, falseLabel);
  }
}
