package org.lilystudio.smarty4j.statement.modifier;

import java.util.regex.Pattern;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 过滤HTML字符串中的标签。
 * 
 * <pre>
 * {&quot;Blind Woman Gets &lt;font face=&quot;helvetica&gt;&quot;&gt;New Kidney&lt;/font&gt; from Dad she Hasn't Seen in &lt;b&gt;years&lt;/b&gt;.|strip_tags}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * Blind Woman Gets New Kidney from Dad she Hasn't Seen in years.
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $strip_tags extends Modifier {

  /** HTML标签的正则表达式 */
  private static Pattern p = Pattern
      .compile("</?\\p{Alpha}+ *( +\\p{Alpha}+ *=(\"(\\\\.|[\\x00-\\x21\\x23-\\x5b\\u005d-\\uffff])*\"|'(\\\\.|[\\x00-\\x26\\x28-\\x5b\\u005d-\\uffff])*'|[\\x00-\\x3d\\u003f-\\uffff]*) *)*>");

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    return p.matcher(obj.toString()).replaceAll("");
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }
}