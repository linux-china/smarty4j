package org.lilystudio.smarty4j;

import static org.objectweb.asm.Opcodes.*;

import org.lilystudio.smarty4j.statement.IFunction;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 公共函数库。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public final class Utilities {

  /**
   * 正则表达式替换字符串标准化，将'\'替换成'\\'，将'$'替换成'\$'。
   * 
   * @param text
   *          原始字符串文本
   * @return 转义后的字符串文本
   */
  public static String escapeReg(String text) {
    int len = text.length();
    StringBuilder s = new StringBuilder(len * 2);
    for (int i = 0; i < len; i++) {
      char c = text.charAt(i);
      if ((c == '\\') || (c == '$')) {
        s.append('\\');
      }
      s.append(c);
    }
    return s.toString();
  }

  /**
   * 向对象数组中指定位置设置对象，如果数组对象长度不够将扩展数组对象的长度，
   * 本方法不是线程安全的。
   * 
   * @param words
   *          数组对象
   * @param index
   *          添加对象的位置
   * @param o
   *          需要添加的对象
   * @return 保存好数据的数组对象
   */
  public static Object[] setWord(Object[] words, int index, Object o) {
    if (index >= words.length) {
      Object[] copy = new Object[index * 2];
      System.arraycopy(words, 0, copy, 0, index);
      words = copy;
    }
    words[index] = o;
    return words;
  }

  /**
   * 设置布尔表达式短路算法(short-circuit)的语句栈，如果没有指定短路位置，
   * 则向语句栈中添加1与0分别表示<tt>true</tt>与<tt>false</tt>。
   * 
   * @param mw
   *          ASM方法访问对象
   * @param jumpInsn
   *          JVM比较跳转指令
   * @param trueLabel
   *          表达式结果为真时的短路位置
   * @param falseLabel
   *          表达式结果为假时的短路位置
   */
  public static void visitSCInsn(MethodVisitor mw, int jumpInsn,
      Label trueLabel, Label falseLabel) {
    boolean hasNotTrue = trueLabel == null;
    boolean hasNotFalse = falseLabel == null;

    // 判断条件表达式是否为真, 如果是优先考虑短路算法
    if (hasNotTrue) {
      trueLabel = new Label();
    }
    mw.visitJumpInsn(jumpInsn, trueLabel);

    // 判断条件表达式是否为假, 如果是优先考虑短路算法
    if (hasNotFalse) {
      falseLabel = new Label();
      mw.visitInsn(ICONST_0);
    }
    mw.visitJumpInsn(GOTO, falseLabel);

    // 非短路情况下的真值插入
    if (hasNotTrue) {
      mw.visitLabel(trueLabel);
      mw.visitInsn(ICONST_1);
    }

    if (hasNotFalse) {
      mw.visitLabel(falseLabel);
    }
  }

  /**
   * 向JVM语句栈中放入一个常量整数值, JVM中对小常量整数的处理有特殊的优化,
   * 因此单独写成一个方法, 根据常量的值选择不同的JVM语句
   * 
   * @param mw
   *          ASM方法访问对象
   * @param value
   *          常量整数值
   */
  public static void visitILdcInsn(MethodVisitor mw, int value) {
    switch (value) {
    case -1:
      mw.visitInsn(ICONST_M1);
      break;
    case 0:
      mw.visitInsn(ICONST_0);
      break;
    case 1:
      mw.visitInsn(ICONST_1);
      break;
    case 2:
      mw.visitInsn(ICONST_2);
      break;
    case 3:
      mw.visitInsn(ICONST_3);
      break;
    case 4:
      mw.visitInsn(ICONST_4);
      break;
    case 5:
      mw.visitInsn(ICONST_5);
      break;
    default:
      if (value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE) {
        mw.visitIntInsn(BIPUSH, value);
      } else if (value <= Short.MAX_VALUE && value >= Short.MIN_VALUE) {
        mw.visitIntInsn(SIPUSH, value);
      } else {
        mw.visitLdcInsn(value);
      }
    }
  }

  /**
   * 从指定的节点开始，向父节点遍历查找指定的类。
   * 
   * @param now 开始查找的节点
   * @param clazz
   *          希望父节点匹配的类对象
   * @return 如果某一级父节点属于指定的类，则返回这个父节点，否则返回null
   */
  public static Object find(IFunction now, Class<?> clazz) {
    for (; now != null; now = now.getParent()) {
      if (clazz.isInstance(now)) {
        return now;
      }
    }
    return null;
  }
}
