package org.lilystudio.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 非操作布尔表达式节点, 将JVM语句栈内的布尔表达式逻辑值设置成相反的值
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class NotCheck extends CheckExpression {

  /** 需要非操作的表达式 */
  private CheckExpression exp;

  /**
   * 创建非操作布尔表达式节点
   * 
   * @param exp
   *          需要非操作的表达式
   */
  public NotCheck(IExpression exp) {
    this.exp = new TranslateCheck(exp);
  }

  public void setCheckLabel(Label trueLabel, Label falseLabel) {
    exp.setCheckLabel(falseLabel, trueLabel);
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    exp.parse(mw, local, variableNames);
    mw.visitInsn(ICONST_1);
    mw.visitInsn(SWAP);
    mw.visitInsn(ISUB);
  }

  public void scan(Template template) {
    exp.scan(template);
  }
}