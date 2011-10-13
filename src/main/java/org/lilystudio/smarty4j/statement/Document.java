package org.lilystudio.smarty4j.statement;

import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.TemplateReader;

/**
 * 文档语句，它表示整个文档节点树的根节点。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class Document extends BlockStatement {

  /**
   * 创建文档树。
   * 
   * @param template
   *          文档节点对应的模板对象
   * @param in
   *          文本输入对象
   */
  public Document(Template template, TemplateReader in) {
    this(template, in, null);
  }

  /**
   * 创建文档树。
   * 
   * @param template
   *          文档节点对应的模板对象
   * @param in
   *          文本输入对象
   * @param parent
   *          文档树的父节点
   */
  public Document(Template template, TemplateReader in, IBlockFunction parent) {
    if (parent != null) {
      try {
        setParent(parent);
      } catch (ParseException e) {
        in.addMessage(e);
      }
    }
    Engine engine = template.getEngine();
    process(template, in, engine.getLeftDelimiter(), engine.getRightDelimiter());
  }
}