package org.lilystudio.smarty4j.statement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lilystudio.smarty4j.Analyzer;
import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.Operation;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.TemplateReader;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.ObjectExpression;
import org.objectweb.asm.MethodVisitor;

/**
 * 区块函数语句节点虚基类，区块函数指的是函数内部包含其它函数或文本，需要拥有结束标签的函数，
 * 在模板分析过程中，系统首先调用函数的初始化方法，然后解析函数的参数，
 * 然后设置函数的父函数，最后解析函数的内部数据。
 * 
 * @see org.lilystudio.smarty4j.NullWriter
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public abstract class Block extends Function implements IBlockFunction {

  /** ASM名称 */
  public static final String NAME = Block.class.getName().replace('.', '/');

  /** 函数区块中的全部子语句 */
  protected List<IStatement> children;

  public void addStatement(IStatement child) throws ParseException {
    if (children == null) {
      children = new ArrayList<IStatement>();
    }
    children.add(child);
  }

  @Override
  public void process(Template template, TemplateReader in, String left,
      String right) {
    StringBuilder text = new StringBuilder(64);

    Engine engine = template.getEngine();
    boolean debug = engine.isDebug();
    next: while (true) {
      String line;

      try {
        line = in.readLine();
      } catch (IOException e) {
        // 出现异常的概率极低
        throw new RuntimeException("数据读入异常");
      }

      if (line == null) {
        // 文档已经结束, 如果不是文档函数本身, 则块状函数没有正常结束
        String name = getName();
        if (name == null) {
          try {
            addStatement(new TextStatement(text.toString()));
          } catch (ParseException e) {
            in.addMessage(e);
          }
        } else {
          in.addMessage("没有找到" + name + "的结束标签");
        }
        return;
      }

      try {
        // 对smarty标签进行词法分析
        Object[] words = Analyzer.lexical(line, left, right);
        if (words != null) {
          int size = words.length;
          int wordSize = 2;
          for (int i = 2; i < size;) {
            words[wordSize++] = words[i];
            if (words[i] instanceof ObjectExpression) {
              try {
                i = Analyzer.mergeModifier(engine, template, words, i + 1,
                    size, (ObjectExpression) words[i]);
              } catch (ParseException e) {
                in.addMessage(e);
                continue next;
              }
            } else {
              i++;
            }
          }

          int leftStart = (Integer) words[0];
          int rightEnd = (Integer) words[1];
          text.append(line.substring(0, leftStart));
          // 将当前标签至上一个标签之间的内容设置成文本节点
          addStatement(new TextStatement(text.toString()));
          in.move(leftStart);
          text.setLength(0);
          in.unread(line.substring(rightEnd));
          if (wordSize > 2) {
            Object word2 = words[2];
            // 区块函数的结束标签
            if (Operation.C_DIV == word2) {
              // 结束标签内不能有参数, 必须是{/[NAME]}的方式,
              // 并且结束标签必须与当前块函数相同
              String name = getName();
              Object word3 = words[3];
              if ((wordSize == 4) && word3.equals(name)) {
                return;
              }
              if (name == null) {
                in.addMessage("多余的结束标签");
              } else {
                in.addMessage("错误的结束标签");
              }
            } else {
              // 结束标签没有实质上的处理, 不标记行号,
              // 只要不是结束标签就要标记行号
              if (debug && in.isNewline()) {
                addStatement(new DebugStatement(in.getLineNumber()));
              }
              // smarty的注释语法是两头均为'*'号
              if (Operation.C_MUL == word2) {
                if (wordSize == 3 || Operation.C_MUL != words[wordSize - 1]) {
                  in.addMessage("错误的注释语法");
                }
              } else if (word2 instanceof IExpression) {
                // 如果第一个词是对象表达式, 则是输出语句
                addStatement(new PrintStatement(wordSize > 3 ? Analyzer
                    .mergeExpression(words, 2, wordSize, Operation.OBJECT)
                    : (IExpression) word2));
              } else if (word2 instanceof String) {
                String word = (String) word2;
                // 首个词是字符串, 是函数开始语句
                IFunction function = (IFunction) engine.createNode(word, true);
                function.init(template, word);
                function.syntax(template, words, wordSize);
                if (function.setParent(this)) {
                  addStatement(function);
                }
                function.process(template, in, left, right);
              } else {
                try {
                  addStatement(new PrintStatement(Analyzer.mergeExpression(
                      words, 2, wordSize, Operation.OBJECT)));
                } catch (ParseException e) {
                  in.addMessage("无法识别的语法");
                }
              }
            }
          } else {
            in.addMessage("无法识别的语法");
          }
        } else {
          text.append(line);
        }
      } catch (ParseException e) {
        in.addMessage(e);
      } catch (Exception e) {
        in.addMessage(e.getMessage());
      }
    }
  }

  @Override
  public void scan(Template template) {
    super.scan(template);
    if (children != null) {
      for (IStatement statement : children) {
        statement.scan(template);
      }
    }
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    if (children != null) {
      for (IStatement child : children) {
        child.parse(mw, local, variableNames);
      }
    }
  }
}