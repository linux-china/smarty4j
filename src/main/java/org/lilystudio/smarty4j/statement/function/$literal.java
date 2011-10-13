package org.lilystudio.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.TemplateReader;
import org.lilystudio.smarty4j.statement.Function;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.objectweb.asm.MethodVisitor;

/**
 * 忽略函数中内容的Smarty语法，将函数中的内容直接输出。
 * 
 * <pre>
 * {literal}
 * &lt;table border=0&gt;
 *   &lt;tr&gt;
 *     &lt;td&gt;
 *       &lt;a href=&quot;{$url}&quot;&gt;
 *         &lt;font color=&quot;red&quot;&gt;This is a test&lt;/font&gt;
 *       &lt;/a&gt;
 *     &lt;/td&gt;
 *   &lt;/tr&gt;
 * &lt;/table&gt;
 * {/literal}
 * 
 * &lt;b&gt;OUTPUT:&lt;/b&gt;
 * &lt;table border=0&gt;
 *   &lt;tr&gt;
 *     &lt;td&gt;
 *       &lt;a href=&quot;{$url}&quot;&gt;
 *         &lt;font color=&quot;red&quot;&gt;This is a test&lt;/font&gt;
 *       &lt;/a&gt;
 *     &lt;/td&gt;
 *   &lt;/tr&gt;
 * &lt;/table&gt;
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $literal extends Function {

  /** 结束符 */
  private static Pattern p = Pattern.compile("\\{ */ *literal *\\}");

  /** 文本缓冲区 */
  private StringBuilder text = new StringBuilder(64);

  @Override
  public void process(Template template, TemplateReader in, String left,
      String right) {
    while (true) {
      String line;
      try {
        line = in.readLine();
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
      }

      if (line == null) {
        in.addMessage("没有找到literal的结束标签");
        return;
      }

      Matcher m = p.matcher(line);
      if (m.find()) {
        text.append(line.substring(0, m.start()));
        in.unread(line.substring(m.end()));
        return;
      }

      text.append(line);
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    mw.visitVarInsn(ALOAD, WRITER);
    mw.visitLdcInsn(text.toString());
    mw.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write",
        "(Ljava/lang/String;)V");
  }
}
