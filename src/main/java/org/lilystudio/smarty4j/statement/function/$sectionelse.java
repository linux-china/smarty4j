package org.lilystudio.smarty4j.statement.function;

import java.util.Map;

import org.lilystudio.smarty4j.statement.Function;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.lilystudio.smarty4j.statement.ParentType;
import org.objectweb.asm.MethodVisitor;

/**
 * 参见section函数。
 * 
 * @see org.lilystudio.smarty4j.statement.function.$section
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
@ParentType(name = "section")
public class $sectionelse extends Function {

  public ParameterCharacter[] getDefinitions() {
    return null;
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
  }
}
