package org.lilystudio.smarty4j.statement.function;

import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.number.ConstInteger;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 日期格式转换。
 * 
 * <pre>
 * date--日期数据源，可以是Date对象，也可以是字符串
 * from--date如果是字符串，这里用于描述字符串的日期格式
 * to--转换输出的结果格式，输入输出的日期格式请参考Java的SimpleDateFormat
 * locale--当前使用的国家地区格式，如果省略将使用服务器的缺省设置
 * assign--结果输出的变量名称，如果省略直接输出至输出流中
 * timezone--当前所在的时区
 * 
 * {date_format from=&quot;yyyy-MM-dd&quot; to=&quot;MM/dd/yyyy HH:mm:ss&quot; date=&quot;2006-01-01&quot; timezone=8}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * 01/01/2006 08:00:00
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $date_format extends LineFunction {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.OBJECT, "date"),
      new ParameterCharacter(ParameterCharacter.STRING, null, "from"),
      new ParameterCharacter(ParameterCharacter.STROBJECT, null, "to"),
      new ParameterCharacter(ParameterCharacter.OBJECT, null, "locale"),
      new ParameterCharacter(ParameterCharacter.STRING, null, "assign"),
      new ParameterCharacter(ParameterCharacter.INTOBJECT, new ConstInteger(0),
          "timezone") };

  @Override
  public void execute(Context context, Writer writer, Object[] values)
      throws Exception {
    // 设置输出格式
    SimpleDateFormat simple;
    if (values[3] != null) {
      if (values[3] instanceof Locale) {
        simple = new SimpleDateFormat("", (Locale) values[3]);
      } else {
        throw new RuntimeException("locale不是java.util.Locale对象");
      }
    } else {
      simple = new SimpleDateFormat("");
    }

    Object date;
    if (values[0] instanceof Date) {
      date = values[0];
    } else {
      // 进行时区转换
      if (values[1] == null) {
        date = new Date(Long.parseLong(values[0].toString()) / 1000
            + ((Integer) values[5]) * 60 * 60 * 1000);
      } else {
        simple.applyPattern((String) values[1]);
        Date result = simple.parse(values[0].toString());
        result.setTime(result.getTime() + ((Integer) values[5]) * 60 * 60
            * 1000);
        date = result;
      }
    }

    if (values[2] != null) {
      simple.applyPattern((String) values[2]);
      date = simple.format((Date) date);
    }

    if (values[4] != null) {
      context.set((String) values[4], date);
    } else {
      writer.write(date.toString());
    }
  }

  @Override
  public void syntax(Template template, Object[] words, int wordSize)
      throws ParseException {
    super.syntax(template, words, wordSize);

    if ((getParameter(2) == null) && (getParameter(3) == null)) {
      throw new ParseException("'to'和'assign'必须定义一个");
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }

  @Override
  public void scan(Template template) {
    super.scan(template);
    if (getParameter(4) != null) {
      template.preventCacheVariable(getParameter(4).toString());
    }
  }
}