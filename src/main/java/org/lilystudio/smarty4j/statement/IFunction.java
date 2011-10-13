package org.lilystudio.smarty4j.statement;

import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.TemplateReader;

/**
 * 函数节点，函数节点的初始化顺序是首先调用init方法设置基本信息，
 * 然后调用syntax方法进行函数的参数设置，
 * 然后调用setParent方法设置父节点信息，最后调用process方法解析输入流。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public interface IFunction extends IStatement, IParameter {

  /**
   * 获取函数名称。
   * 
   * @return 函数名称
   */
  String getName();

  /**
   * 获取父节点。
   * 
   * @return 父节点
   */
  IBlockFunction getParent();

  /**
   * 函数初始化。
   * 
   * @param template
   *          可以用于保存函数对象的模板，函数对象自己决定是否需要与模板建立关联
   * @param name
   *          函数名称
   * @see org.lilystudio.smarty4j.Template#addNode
   */
  void init(Template template, String name);

  /**
   * 将词法分析的结果进行语法分析处理并形成函数节点，如果有特殊的语法，例如if函数，
   * 需要重载这个方法。
   * 
   * @param template
   *          模板
   * @param words
   *          词法分析结果
   * @param wordSize
   *          词法分析结果数量
   * @throws ParseException
   *           参数不合法
   */
  void syntax(Template template, Object[] words, int wordSize)
      throws ParseException;

  /**
   * 设置父节点，有些函数对父节点有特殊的要求，重新实现这个方法，
   * 例如elseif只能是if下的子节点。
   * 
   * @param parent
   *          父节点
   * @return 如果需要引擎自动关联父子节点的关系返回true。某些情况下，
   *         子结点需要关联父节点信息，但不希望它自己本身被父节点引用，
   *         请返回false，父节点需要引用手动调用addStatement方法
   * @throws ParseException
   *           父节点不合法
   */
  boolean setParent(IBlockFunction parent) throws ParseException;

  /**
   * 对文本输入流进行解析，如果区块函数内部有特殊的解析规则，例如literal，
   * 单行函数也可能需要操作输入流，例如macro，请重载这个函数。
   * 在函数的初始化完全完成后，才被调用这个方法解析输入流。
   * 
   * @param template
   *          模板对象
   * @param in
   *          文本输入对象
   * @param left
   *          Smarty语法左分隔符
   * @param right
   *          Smarty语法右分隔符
   */
  void process(Template template, TemplateReader in, String left, String right);
}
