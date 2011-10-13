package org.lilystudio.smarty4j.expression;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.statement.IModifier;
import org.objectweb.asm.MethodVisitor;

/**
 * 变量调节器扩展节点
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class ModifierExtended implements IExtended {

  private IModifier modifier;

  public ModifierExtended(IModifier modifier) {
    this.modifier = modifier;
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    modifier.parse(mw, local, variableNames);
  }

  public void scan(Template template) {
    modifier.scan(template);
  }
}