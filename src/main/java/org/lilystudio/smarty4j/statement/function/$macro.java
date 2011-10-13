package org.lilystudio.smarty4j.statement.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.TemplateReader;
import org.lilystudio.smarty4j.statement.Function;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.objectweb.asm.MethodVisitor;

/**
 * 直接加载一个子文件，与include函数不同之处在于，
 * 使用macro加载的文件分析得到的节点树，将作为当前模板的一个子节点而存在。
 * 
 * @see org.lilystudio.smarty4j.statement.IBlockFunction#find(Class)
 * @see org.lilystudio.smarty4j.statement.function.$include
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $macro extends Function {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = { new ParameterCharacter(
      ParameterCharacter.STRING, "file") };

  @Override
  public void process(Template template, TemplateReader in, String left,
      String right) {
    String name = getParameter(0).toString();
    String path = template.getPath(name, false);
    File file = new File(path);
    template.associate(file);
    try {
      in.insertReader(name, new TemplateReader(new InputStreamReader(
          new FileInputStream(file), template.getEngine().getEncoding())));
    } catch (IOException e) {
      in.addMessage("文件打开错误");
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
  }
}