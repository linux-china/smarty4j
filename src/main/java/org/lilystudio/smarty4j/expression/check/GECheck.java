package org.lilystudio.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import org.lilystudio.smarty4j.Utilities;
import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.MethodVisitor;

/**
 * 大于等于操作布尔表达式节点, 检测第一个表达式是否弱类型大于等于第二个表达式
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class GECheck extends BinaryCheck {

  /**
   * 创建大于等于操作布尔表达式节点
   * 
   * @param exp1
   *          表达式1
   * @param exp2
   *          表达式2
   */
  public GECheck(IExpression exp1, IExpression exp2) {
    super(exp1, exp2);
  }

  @Override
  protected void checkDouble(MethodVisitor mw, boolean swap) {
    mw.visitInsn(DCMPL);
    Utilities.visitSCInsn(mw, swap ? IFLE : IFGE, trueLabel, falseLabel);
  }

  @Override
  protected void checkString(MethodVisitor mw, boolean swap) {
    mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "compareTo",
        "(Ljava/lang/String;)I");
    Utilities.visitSCInsn(mw, swap ? IFLE : IFGE, trueLabel, falseLabel);
  }
}
