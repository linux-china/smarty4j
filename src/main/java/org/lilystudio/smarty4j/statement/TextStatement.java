package org.lilystudio.smarty4j.statement;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.objectweb.asm.MethodVisitor;

/**
 * 文本输出语句，简单的将模板文件中的文本进行输出。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class TextStatement implements IStatement {

  /** 需要输出的文本 */
  private String text;

  /**
   * 建立文本输出语句。
   * 
   * @param text
   *          需要输出的文本
   */
  public TextStatement(String text) {
    if (text.length() > 0) {
      this.text = text;
    }
  }

  /**
   * 获取需要输出的文本。
   * 
   * @return 需要输出的文本
   */
  public String getText() {
    return text;
  }

  public void scan(Template template) {
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    if (text != null) {
      mw.visitVarInsn(ALOAD, WRITER);
      mw.visitLdcInsn(text);
      mw.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write",
          "(Ljava/lang/String;)V");
    }
  }
}