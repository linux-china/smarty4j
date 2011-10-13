package org.lilystudio.smarty4j.statement;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.DUP;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.Utilities;
import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.MethodVisitor;

/**
 * 包含参数定义的节点虚基类。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public abstract class Parameter implements IParameter {

  /** 函数的参数 */
  private IExpression[] parameters;

  public IExpression getParameter(int index) {
    return parameters[index];
  }

  public void setParameters(IExpression[] expressions) {
    parameters = expressions;
  }

  public void parseAllParameters(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    if ((parameters == null) || (parameters.length == 0)) {
      // 没有参数
      mw.visitInsn(ACONST_NULL);
    } else {
      Utilities.visitILdcInsn(mw, parameters.length);
      mw.visitTypeInsn(ANEWARRAY, "java/lang/Object");

      for (int i = 0; i < parameters.length; i++) {
        if (parameters[i] == null) {
          continue;
        }
        mw.visitInsn(DUP);
        Utilities.visitILdcInsn(mw, i);
        parameters[i].parseObject(mw, local, variableNames);
        mw.visitInsn(AASTORE);
      }
    }
  }

  public void scan(Template template) {
    if (parameters != null) {
      for (IExpression exp : parameters) {
        if (exp != null) {
          exp.scan(template);
        }
      }
    }
  }
}
