package org.lilystudio.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.statement.Function;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.objectweb.asm.MethodVisitor;

/**
 * 转义函数，输出左边界符。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $ldelim extends Function {

  /** 左边界符 */
  private String delimiter;

  @Override
  public void syntax(Template template, Object[] words, int wordSize) {
    delimiter = template.getEngine().getLeftDelimiter();
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    mw.visitVarInsn(ALOAD, WRITER);
    mw.visitLdcInsn(delimiter);
    mw.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write",
        "(Ljava/lang/String;)V");
  }
}