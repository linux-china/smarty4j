package org.lilystudio.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 或操作对象表达式节点, 如果某一个对象表达式为<tt>true</tt>, 返回这个对象,
 * 否则返回最后一个对象
 * 
 * @version 0.1.4, 2009/03/01
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class ObjectOrExpression extends ObjectCheckExpression {

  /**
   * 创建或操作布尔表达式节点
   * 
   * @param exp1
   *          表达式1
   * @param exp2
   *          表达式2
   */
  public ObjectOrExpression(IExpression exp1, IExpression exp2) {
    super(exp1, exp2);
  }

  public void parseCheck(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    Label isTrue = trueLabel != null ? trueLabel : new Label();

    // exp1为假的时候不进行短路处理
    exp1.setCheckLabel(isTrue, null);
    exp1.parseCheck(mw, local, variableNames);
    mw.visitJumpInsn(IFNE, isTrue);

    // exp1已经为假, 因此exp2允许全部的短路处理
    exp2.setCheckLabel(isTrue, falseLabel);
    exp2.parseCheck(mw, local, variableNames);
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

  public void parseSelf(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    Label end = new Label();

    exp1.parseObject(mw, local, variableNames);
    mw.visitInsn(DUP);
    mw.visitMethodInsn(INVOKESTATIC, NAME, "toCheck", "(Ljava/lang/Object;)Z");
    mw.visitJumpInsn(IFNE, end);

    mw.visitInsn(POP);
    exp2.parseObject(mw, local, variableNames);

    mw.visitLabel(end);
  }
}