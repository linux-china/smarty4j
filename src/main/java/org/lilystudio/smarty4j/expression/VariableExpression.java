package org.lilystudio.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Template;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 变量表达式节点, 向JVM语句栈内放入从数据容器中引用的对象
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class VariableExpression extends ObjectExpression {

  /** 变量在数据容器中的名称 */
  private String name;

  /**
   * 创建建变量表达式节点
   * 
   * @param name
   *          变量的名称
   */
  public VariableExpression(String name) {
    this.name = name;
  }

  public void setCheckLabel(Label trueLabel, Label falseLabel) {
  }

  public void scan(Template template) {
    super.scan(template);
    template.addUsedVariable(name);
  }
  
  public void parseSelf(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    Integer index = variableNames == null ? null : variableNames.get(name);
    if (index != null) {
      // 如果对象有堆栈中的索引号, 直接从堆栈中取出对象的值
      mw.visitVarInsn(ALOAD, index);
    } else {
      // 从数据容器中获取对象的值
      mw.visitVarInsn(ALOAD, CONTEXT);
      mw.visitLdcInsn(name);
      mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
          "(Ljava/lang/String;)Ljava/lang/Object;");
    }
  }
}