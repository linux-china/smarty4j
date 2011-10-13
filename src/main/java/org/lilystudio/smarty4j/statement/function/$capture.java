package org.lilystudio.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Block;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.lilystudio.util.StringWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * 捕获输出流，重定向输出至变量中。
 * 
 * <pre>
 * name--输出的变量名，默认值为default，变量通过$smarty.capture.[name]来访问
 * assign--输出的模板数据名，如果设置了assign，变量通过$[assign]访问，name的设置将失效
 * 
 * {capture}commands must be paired{/capture}
 * {$smarty.capture.default}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * commands must be paired
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $capture extends Block {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STRING, new StringExpression(
          "default"), "name"),
      new ParameterCharacter(ParameterCharacter.STRING, null, "assign") };

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }

  @Override
  public void scan(Template template) {
    super.scan(template);
    if (getParameter(1) != null) {
      template.preventCacheVariable(getParameter(1).toString());
    }
  }

  @Override
  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    // if (assign != null) {
    // context.set(assign, childWriter);
    // } else {
    // context.getCaptures().put(name,
    // childWriter);
    // }
    mw.visitVarInsn(ALOAD, WRITER);
    mw.visitVarInsn(ALOAD, CONTEXT);
    IExpression assign = getParameter(1);
    if (assign != null) {
      assign.parse(mw, local, variableNames);
    } else {
      mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "getCaptures",
          "()Ljava/util/Map;");
      getParameter(0).parse(mw, local, variableNames);
    }
    mw.visitTypeInsn(NEW, StringWriter.NAME);
    mw.visitInsn(DUP);
    mw.visitMethodInsn(INVOKESPECIAL, StringWriter.NAME, "<init>", "()V");
    mw.visitVarInsn(ASTORE, WRITER);
    super.parse(mw, local, variableNames);
    mw.visitVarInsn(ALOAD, WRITER);
    mw.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "toString",
        "()Ljava/lang/String;");
    if (assign != null) {
      mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
          "(Ljava/lang/String;Ljava/lang/Object;)V");
    } else {
      mw.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put",
          "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
      mw.visitInsn(POP);
    }
    mw.visitVarInsn(ASTORE, WRITER);
  }
}