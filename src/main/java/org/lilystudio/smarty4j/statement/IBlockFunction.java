package org.lilystudio.smarty4j.statement;

import org.lilystudio.smarty4j.ParseException;

/**
 * 区块函数节点。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public interface IBlockFunction extends IFunction {

  /**
   * 向区块语句内部增加子语句。
   * 
   * @param child
   *          需要增加到区块中的语句
   * @throws ParseException
   *           区块中不允许增加这种类型的语句
   */
  void addStatement(IStatement child) throws ParseException;
}
