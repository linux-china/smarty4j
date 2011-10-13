package org.lilystudio.smarty4j.expression.number;

import static org.objectweb.asm.Opcodes.*;

import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.MethodVisitor;


/**
 * 整数减法操作表达式节点, 向语句栈中放入两个表达式进行减法操作结果
 * 
 * @version 0.1.1, 2007/03/24
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class SubInteger extends BinaryInteger {

  /**
   * 创建整数减法操作表达式节点
   * 
   * @param exp1
   *          表达式1
   * @param exp2
   *          表达式2
   */
  public SubInteger(IExpression exp1, IExpression exp2) {
    super(exp1, exp2);
  }

  @Override
  protected void process(MethodVisitor mw) {
    mw.visitInsn(ISUB);
  }
}
