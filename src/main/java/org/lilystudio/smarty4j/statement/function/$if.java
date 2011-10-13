package org.lilystudio.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lilystudio.smarty4j.Analyzer;
import org.lilystudio.smarty4j.Operation;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.check.CheckExpression;
import org.lilystudio.smarty4j.expression.check.TranslateCheck;
import org.lilystudio.smarty4j.statement.Block;
import org.lilystudio.smarty4j.statement.BlockStatement;
import org.lilystudio.smarty4j.statement.IBlockFunction;
import org.lilystudio.smarty4j.statement.IStatement;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 条件判断函数，完整的书写格式如下：
 * 
 * <pre>
 * {if 条件1}
 * 表达式1
 * {elseif 条件2}
 * 表达式2
 * {elseif 条件3}
 * 表达式3
 * ...
 * {else}
 * 表达式
 * {/if}
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $if extends Block {

  /**
   * 单个的if/elseif条件分支结构块类
   */
  private class Branch {

    /** 分支结构块中的条件表达式 */
    private CheckExpression check;

    /** 分支结构块中的区块语句 */
    private IBlockFunction block = new BlockStatement();

    /**
     * 分支结构块构造函数
     * 
     * @param check
     *          结构块条件, 如果check为NULL, 表示else块
     * @param block
     *          区块语句
     */
    private Branch(CheckExpression check) {
      this.check = check;
    }
  }

  /** if/elseif条件分支结构块 */
  private List<Branch> branchs = new ArrayList<Branch>();

  /** else结构块 */
  private IBlockFunction elseBlock;

  /** 分支结构块 */
  private IBlockFunction now;

  @Override
  public void addStatement(IStatement statement) throws ParseException {
    if (statement instanceof $elseif) {
      if (elseBlock != null) {
        throw new ParseException("在else语句后不能再包含其它elseif或else语句");
      }
      addBranch((($elseif) statement).getCheckExpression());
    } else if (statement instanceof $else) {
      if (elseBlock != null) {
        throw new ParseException("在else语句后不能再包含其它elseif或else语句");
      }
      elseBlock = new BlockStatement();
      elseBlock.setParent(this);
      now = elseBlock;
    } else {
      now.addStatement(statement);
    }
  }

  @Override
  public void syntax(Template template, Object[] words, int wordSize)
      throws ParseException {
    addBranch(new TranslateCheck(Analyzer.mergeExpression(words, 3, wordSize,
        Operation.FLOAT)));
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }

  @Override
  public void scan(Template template) {
    super.scan(template);
    for (Branch branch : branchs) {
      branch.check.scan(template);
      branch.block.scan(template);
    }
    if (elseBlock != null) {
      elseBlock.scan(template);
    }
  }

  @Override
  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    Iterator<Branch> i = branchs.iterator();
    Branch statement;

    Label end = new Label();
    while (i.hasNext()) {
      statement = i.next();
      Label next = new Label();
      Label block = new Label();
      statement.check.setCheckLabel(block, next);
      statement.check.parse(mw, local, variableNames);
      mw.visitJumpInsn(IFEQ, next);
      mw.visitLabel(block);
      statement.block.parse(mw, local, variableNames);
      mw.visitJumpInsn(GOTO, end);
      mw.visitLabel(next);
    }

    if (elseBlock != null) {
      elseBlock.parse(mw, local, variableNames);
    }

    mw.visitLabel(end);
  }

  /**
   * 增加一个分支结构块
   * 
   * @param check
   *          分支结构块的逻辑表达式
   * @throws ParseException
   *           设置父对象异常
   */
  private void addBranch(CheckExpression check) throws ParseException {
    Branch branch = new Branch(check);
    branch.block.setParent(this);
    now = branch.block;
    branchs.add(branch);
  }
}