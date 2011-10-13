package org.lilystudio.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 或操作布尔表达式节点, 检测两个表达式的或操作结果
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class OrCheck extends BinaryCheckExpression {

  /**
   * 创建或操作布尔表达式节点
   * 
   * @param exp1
   *          表达式1
   * @param exp2
   *          表达式2
   */
  public OrCheck(IExpression exp1, IExpression exp2) {
    super(new TranslateCheck(exp1), new TranslateCheck(exp2));
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    Label isTrue = trueLabel != null ? trueLabel : new Label();

    // exp1为假的时候不进行短路处理
    exp1.setCheckLabel(isTrue, null);
    exp1.parse(mw, local, variableNames);
    mw.visitJumpInsn(IFNE, isTrue);

    // exp1已经为假, 因此exp2允许全部的短路处理
    exp2.setCheckLabel(isTrue, falseLabel);
    exp2.parse(mw, local, variableNames);
    mw.visitJumpInsn(IFNE, isTrue);

    Label end = new Label();
    mw.visitInsn(ICONST_0);
    mw.visitJumpInsn(GOTO, end);

    if (trueLabel == null) {
      mw.visitLabel(isTrue);
      mw.visitInsn(ICONST_1);
    }

    mw.visitLabel(end);
  }
}