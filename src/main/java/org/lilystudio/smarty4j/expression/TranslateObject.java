package org.lilystudio.smarty4j.expression;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 对象转换节点, 将表达式转换成对象表达式
 * 
 * @version 0.1.4, 2009/03/01
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class TranslateObject extends ObjectExpression {

  /** 需要转换的表达式 */
  private IExpression exp;

  /**
   * 建立对象转换节点
   * 
   * @param exp
   *          字符串常量
   */
  public TranslateObject(IExpression exp) {
    this.exp = exp;
  }

  public void scan(Template template) {
    super.scan(template);
    exp.scan(template);
  }

  @Override
  public void parseString(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    exp.parseString(mw, local, variableNames);
  }

  public void setCheckLabel(Label trueLabel, Label falseLabel) {
    exp.setCheckLabel(trueLabel, falseLabel);
  }

  public void parseCheck(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    exp.parseCheck(mw, local, variableNames);
  }
  
  public void parseSelf(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    exp.parseObject(mw, local, variableNames);
  }
}
