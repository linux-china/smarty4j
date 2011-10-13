package org.lilystudio.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Analyzer;
import org.lilystudio.smarty4j.Operation;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.check.CheckExpression;
import org.lilystudio.smarty4j.expression.check.TranslateCheck;
import org.lilystudio.smarty4j.statement.Block;
import org.lilystudio.smarty4j.statement.ILoop;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 循环函数，完整的书写格式如下：
 * 
 * <pre>
 * {while 条件1}
 * 表达式
 * {/while}
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $while extends Block implements ILoop {

  /** 循环结构块中的条件表达式 */
  private CheckExpression check;

  /** 循环开始标签 */
  private Label startLabel = new Label();

  /** 循环结束标签 */
  private Label endLabel = new Label();

  public Label getStartLabel() {
    return startLabel;
  }

  public Label getEndLabel() {
    return endLabel;
  }

  public void restore(MethodVisitor mw, Map<String, Integer> variableNames) {
  }

  @Override
  public void syntax(Template template, Object[] words, int wordSize)
      throws ParseException {
    check = new TranslateCheck(Analyzer.mergeExpression(words, 3, wordSize,
        Operation.FLOAT));
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }

  @Override
  public void scan(Template template) {
    super.scan(template);
    check.scan(template);
  }

  @Override
  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    /* 循环语句块开始位置 */
    Label block = new Label();

    check.setCheckLabel(block, endLabel);

    mw.visitLabel(startLabel);
    check.parse(mw, local, variableNames);
    mw.visitJumpInsn(IFEQ, endLabel);

    mw.visitLabel(block);

    super.parse(mw, local, variableNames);
    mw.visitJumpInsn(GOTO, startLabel);

    mw.visitLabel(endLabel);
  }
}