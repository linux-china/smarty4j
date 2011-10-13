package org.lilystudio.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 字符串表达式节点, 向JVM语句栈内放入字符串
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class StringExpression extends ObjectExpression {

  /** 节点布尔表达式为真时的短路标签 */
  private Label trueLabel;

  /** 节点布尔表达式为假时的短路标签 */
  private Label falseLabel;

  /** 字符串常量 */
  private String value;

  /**
   * 创建字符串表达式节点
   * 
   * @param value
   *          字符串常量
   */
  public StringExpression(String value) {
    this.value = value;
  }

  @Override
  public void parseCheck(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    if (value.length() == 0) {
      // 字符串没有内容相当于false
      if (falseLabel == null) {
        mw.visitInsn(ICONST_0);
      } else {
        mw.visitJumpInsn(GOTO, falseLabel);
      }
    } else {
      if (trueLabel == null) {
        mw.visitInsn(ICONST_1);
      } else {
        mw.visitJumpInsn(GOTO, trueLabel);
      }
    }
  }

  @Override
  public void parseString(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    if (isExtended()) {
      super.parseString(mw, local, variableNames);
    } else {
      parse(mw, local, variableNames);
    }
  }

  @Override
  public String toString() {
    return value;
  }

  public void setCheckLabel(Label trueLabel, Label falseLabel) {
    this.trueLabel = trueLabel;
    this.falseLabel = falseLabel;
  }

  public void parseSelf(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    mw.visitLdcInsn(value);
  }
}