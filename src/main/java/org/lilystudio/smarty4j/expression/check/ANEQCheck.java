package org.lilystudio.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Utilities;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.ObjectExpression;
import org.lilystudio.smarty4j.expression.number.DoubleExpression;
import org.lilystudio.smarty4j.expression.number.IntegerExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 全不等于布尔表达式节点, 检测两个表达式的结果是否完全不相等, 不使用弱类型规则
 * 
 * @version 0.1.4, 2009/03/01
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class ANEQCheck extends BinaryCheckExpression {

  /**
   * 建立二元等于操作表达式节点
   * 
   * @param exp1
   *          表达式1
   * @param exp2
   *          表达式2
   */
  public ANEQCheck(IExpression exp1, IExpression exp2) {
    super(exp1, exp2);
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    if ((exp1 instanceof IntegerExpression && exp2 instanceof IntegerExpression)
        || (exp1 instanceof CheckExpression && exp2 instanceof CheckExpression)) {
      exp1.parse(mw, local, variableNames);
      exp2.parse(mw, local, variableNames);
      Utilities.visitSCInsn(mw, IF_ICMPNE, trueLabel, falseLabel);
    } else if (exp1 instanceof DoubleExpression
        && exp2 instanceof DoubleExpression) {
      exp1.parse(mw, local, variableNames);
      exp2.parse(mw, local, variableNames);
      mw.visitInsn(DCMPL);
      Utilities.visitSCInsn(mw, IFNE, trueLabel, falseLabel);
    } else {
      boolean exp1IsObject = exp1 instanceof ObjectExpression;
      boolean exp2IsObject = exp2 instanceof ObjectExpression;
      if (exp1IsObject || exp2IsObject) {
        // if (obj1 == obj2) {
        // return false;
        // } else if (obj1 == null) {
        // return true;
        // } else {
        // return !obj1.equals(obj2);
        // }
        Label isTrue = trueLabel == null ? new Label() : trueLabel;
        Label isFalse = falseLabel == null ? new Label() : falseLabel;
        Label end = new Label();

        boolean hasNotLabel = trueLabel == null && falseLabel == null;
        if (exp1IsObject && exp2IsObject) {
          exp1.parse(mw, local, variableNames);
          exp2.parse(mw, local, variableNames);
          mw.visitVarInsn(ASTORE, local + 1);
          mw.visitVarInsn(ASTORE, local);

          // 判断两个对象是否地址相同
          mw.visitVarInsn(ALOAD, local);
          mw.visitVarInsn(ALOAD, local + 1);
          mw.visitJumpInsn(IF_ACMPEQ, isFalse);

          // 判断第一个对象是否为NULL
          mw.visitVarInsn(ALOAD, local);
          mw.visitJumpInsn(IFNULL, isTrue);

          if (hasNotLabel) {
            mw.visitInsn(ICONST_1);
          }
          // 判断第一个对象是否相等
          mw.visitVarInsn(ALOAD, local);
          mw.visitVarInsn(ALOAD, local + 1);
        } else {
          if (hasNotLabel) {
            mw.visitInsn(ICONST_1);
          }
          if (exp1IsObject) {
            exp2.parseObject(mw, local, variableNames);
            exp1.parse(mw, local, variableNames);
          } else {
            exp1.parseObject(mw, local, variableNames);
            exp2.parse(mw, local, variableNames);
          }
        }
        mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals",
            "(Ljava/lang/Object;)Z");
        if (hasNotLabel) {
          mw.visitInsn(ISUB);
          mw.visitJumpInsn(GOTO, end);
        } else {
          Utilities.visitSCInsn(mw, IFEQ, isTrue, isFalse);
        }

        // 结果输出
        if (trueLabel == null) {
          mw.visitLabel(isTrue);
          mw.visitInsn(ICONST_1);
          mw.visitJumpInsn(GOTO, end);
        }
        if (falseLabel == null) {
          mw.visitLabel(isFalse);
          mw.visitInsn(ICONST_0);
        }

        mw.visitLabel(end);
      } else {
        if (trueLabel == null) {
          mw.visitInsn(ICONST_1);
        } else {
          mw.visitJumpInsn(GOTO, trueLabel);
        }
      }
    }
  }
}
