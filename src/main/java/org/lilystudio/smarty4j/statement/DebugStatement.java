package org.lilystudio.smarty4j.statement;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 调试语句，记录当前处理的行号，在解析错误时，JVM将提示出错的行号信息，
 * 是否开启这个功能受模板控制器的调试开关影响。
 * 
 * @see org.lilystudio.smarty4j.Engine#setDebug(boolean)
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class DebugStatement implements IStatement {

  /** 当前的行号 */
  private int lineNumber;

  /**
   * 创建调试语句
   * 
   * @param lineNumber
   *          当前的行号
   */
  public DebugStatement(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  public void scan(Template template) {
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    Label line = new Label();
    mw.visitLabel(line);
    mw.visitLineNumber(lineNumber, line);
  }
}
