package org.lilystudio.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.number.INumberExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 二元弱类型布尔表达式节点, 向JVM语句栈内放入整数值表示两个对象的弱类型逻辑操作结果
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public abstract class BinaryCheck extends BinaryCheckExpression {

  /** ASM名称 */
  public static final String NAME = BinaryCheck.class.getName().replace('.',
      '/');

  /**
   * 如果变量是能被识别成弱类型浮点数的值则返回<tt>true</tt>
   * 
   * @param o
   *          需要识别的变量
   * @return <tt>true</tt>表示变量能被识别成浮点数
   */
  public static boolean isNumeric(Object o) {
    if ((o != null) && !(o instanceof Number) && !(o.equals(""))) {
      String s = o.toString();
      boolean isDouble = false;
      for (int i = s.length() - 1; i >= 0; i--) {
        char c = s.charAt(i);
        if (c == '.') {
          if (isDouble) {
            return false;
          } else {
            isDouble = true;
          }
        } else if (!Character.isDigit(c)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * 弱类型转换, 将对象转换为浮点数, 其中<tt>null</tt>与空字符串被转换成0.0
   * 
   * @param o
   *          源对象
   * @return 源对象对应的浮点数
   */
  public static double o2d(Object o) {
    if (o == null || o.equals("")) {
      return 0.0d;
    } else if (o instanceof Number) {
      return ((Number) o).doubleValue();
    } else {
      return Double.parseDouble(o.toString());
    }
  }

  /**
   * 弱类型转换, 将对象转换为字符串, 其中<tt>null</tt>被转换成空字符串
   * 
   * @param o
   *          源对象
   * @return 源对象对应的字符串
   */
  public static String o2s(Object o) {
    return o == null ? "" : o.toString();
  }

  /**
   * 创建二元弱类型布尔表达式节点
   * 
   * @param exp1
   *          表达式1
   * @param exp2
   *          表达式2
   */
  public BinaryCheck(IExpression exp1, IExpression exp2) {
    super(exp1, exp2);
  }

  /**
   * 对数值进行二元逻辑操作
   * 
   * @param mw
   *          ASM方法操作者
   * @param swap
   *          true表示交换了两个操作数在栈中的顺序
   */
  protected abstract void checkDouble(MethodVisitor mw, boolean swap);

  /**
   * 对字符串进行二元逻辑操作
   * 
   * @param mw
   *          ASM方法操作者
   * @param swap
   *          true表示交换了两个操作数在栈中的顺序
   */
  protected abstract void checkString(MethodVisitor mw, boolean swap);

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    // 在处理过程中, 如果对象的内容不是字符串, 需要先针对变量进行弱类型转换, 
    // 只有转换不成功的时候才执行字符串比较
    boolean exp1IsNumber = exp1 instanceof INumberExpression;
    boolean exp2IsNumber = exp2 instanceof INumberExpression;
    if (exp1IsNumber && exp2IsNumber) {
      exp1.parseDouble(mw, local, variableNames);
      exp2.parseDouble(mw, local, variableNames);
      checkDouble(mw, false);
    } else if (exp1IsNumber || exp2IsNumber) {
      // if (弱类型(o) instanceof number) {
      // 数值比较exp1与exp2
      // } else {
      // 字符串比较exp1与exp2
      // }
      Label isString = new Label();
      Label end = new Label();

      IExpression first;
      IExpression second;
      if (exp1IsNumber) {
        first = exp2;
        second = exp1;
      } else {
        first = exp1;
        second = exp2;
      }

      first.parse(mw, local, variableNames);
      mw.visitInsn(DUP);

      mw.visitMethodInsn(INVOKESTATIC, NAME, "isNumeric",
          "(Ljava/lang/Object;)Z");
      mw.visitJumpInsn(IFEQ, isString);

      mw.visitMethodInsn(INVOKESTATIC, NAME, "o2d", "(Ljava/lang/Object;)D");
      second.parseDouble(mw, local, variableNames);
      checkDouble(mw, exp1IsNumber);
      mw.visitJumpInsn(GOTO, end);

      mw.visitLabel(isString);
      mw.visitMethodInsn(INVOKESTATIC, NAME, "o2s",
          "(Ljava/lang/Object;)Ljava/lang/String;");
      second.parseString(mw, local, variableNames);
      checkString(mw, exp1IsNumber);

      mw.visitLabel(end);
    } else {
      // if (弱类型(exp1) && 弱类型(exp2) instanceof
      // number) {
      // 数值比较exp1与exp2
      // } else {
      // 字符串比较exp1与exp2
      // }
      Label nonString = new Label();
      Label isString = new Label();
      Label end = new Label();

      exp2.parse(mw, local, variableNames);
      exp1.parse(mw, local, variableNames);
      mw.visitVarInsn(ASTORE, local);
      mw.visitVarInsn(ASTORE, local + 1);

      mw.visitVarInsn(ALOAD, local);
      mw.visitTypeInsn(INSTANCEOF, "java/lang/String");
      mw.visitJumpInsn(IFEQ, nonString);
      mw.visitVarInsn(ALOAD, local + 1);
      mw.visitTypeInsn(INSTANCEOF, "java/lang/String");
      mw.visitJumpInsn(IFNE, isString);

      mw.visitLabel(nonString);
      mw.visitVarInsn(ALOAD, local);
      mw.visitMethodInsn(INVOKESTATIC, NAME, "isNumeric",
          "(Ljava/lang/Object;)Z");
      mw.visitJumpInsn(IFEQ, isString);

      mw.visitVarInsn(ALOAD, local + 1);
      mw.visitMethodInsn(INVOKESTATIC, NAME, "isNumeric",
          "(Ljava/lang/Object;)Z");
      mw.visitJumpInsn(IFEQ, isString);

      mw.visitVarInsn(ALOAD, local);
      mw.visitMethodInsn(INVOKESTATIC, NAME, "o2d", "(Ljava/lang/Object;)D");
      mw.visitVarInsn(ALOAD, local + 1);
      mw.visitMethodInsn(INVOKESTATIC, NAME, "o2d", "(Ljava/lang/Object;)D");
      checkDouble(mw, false);
      mw.visitJumpInsn(GOTO, end);

      mw.visitLabel(isString);
      mw.visitVarInsn(ALOAD, local);
      mw.visitMethodInsn(INVOKESTATIC, NAME, "o2s",
          "(Ljava/lang/Object;)Ljava/lang/String;");
      mw.visitVarInsn(ALOAD, local + 1);
      mw.visitMethodInsn(INVOKESTATIC, NAME, "o2s",
          "(Ljava/lang/Object;)Ljava/lang/String;");
      checkString(mw, false);

      mw.visitLabel(end);
    }
  }
}