package org.lilystudio.smarty4j;

import static org.lilystudio.smarty4j.INode.*;
import static org.objectweb.asm.ClassWriter.*;
import static org.objectweb.asm.Opcodes.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lilystudio.smarty4j.statement.Document;
import org.lilystudio.util.DynamicClassLoader;
import org.lilystudio.util.StringReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * 模板信息类，记录模板文件的来源，时间等基本信息，内置模板解析器的实现接口，
 * 通过调用merge方法, 能够将源数据与模板文件合并产生输出。一般的调用方法如下:<br>
 * <br>
 * 如果是文件需要解析：
 * 
 * <code>
 * Template template = engine.getTemplate(&quot;demo.html&quot;);
 * Writer writer = new StringWriter();
 * template.merge(context, writer);
 * System.out.println(writer.toString());
 * </code>
 * 
 * 如果是字符串需要解析：
 * 
 * <code>
 * Template template = new Template(engine, &quot;22\n{if 1}\n123\n{/if}\n&quot;);
 * Writer writer = new StringWriter();
 * template.merge(context, writer);
 * System.out.println(writer.toString());
 * </code>
 * 
 * @see org.lilystudio.smarty4j.Engine
 * @see org.lilystudio.smarty4j.IParser
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class Template {

  /** ASM名称 */
  public static final String NAME = Template.class.getName().replace('.', '/');

  /** 接口常量字符串数组 */
  private static String[] INTERFACES = { IParser.NAME };

  /** 模板引擎 */
  private Engine engine;

  /** 模板名称，相对模板引擎的地址 */
  private String name;

  /** 模板文件最后更新时间，所有相关文件的最后更新时间 */
  private long lastModified;

  /** 模板文件的路径 */
  private String path;

  /** 模板文件对象 */
  private File file;

  /** 与模板主文件相关联的全部文件名，只要任何一个更新都将重新建立模板类 */
  private List<File> associatedFiles;

  /** 模板对象使用的扩展节点列表，扩展节点支持在解析时被访问 */
  private List<INode> nodes;

  /** 文档对象，位于节点树的最顶层 */
  private INode doc;

  /** 当前模板中读取过的全部变量信息，使用 false 表示不需要缓存读写 */
  private Map<String, Boolean> variables = new HashMap<String, Boolean>();

  /** 文档的解析器接口 */
  private IParser parser;

  /**
   * 根据字符串建立模板对象。
   * 
   * @param engine
   *          模板引擎
   * @param text
   *          字符串
   * @throws TemplateException
   *           模板生成过程中产生错误
   */
  public Template(Engine engine, String text) throws TemplateException {
    this(engine, null, new StringReader(text), true);
  }

  /**
   * 根据输入流建立模板对象。
   * 
   * @param engine
   *          模板引擎
   * @param path
   *          模板文件的路径
   * @param reader
   *          文本读入器
   * @param parse
   *          <tt>true</tt>模板解析后生成解析器;
   *          <tt>false</tt>只是检查语法不生成解析器
   * @throws TemplateException
   *           模板生成过程中产生错误
   */
  public Template(Engine engine, String path, Reader reader, boolean parse)
      throws TemplateException {
    this.engine = engine;
    if (path != null) {
      // 初始化模板的相关信息
      this.path = path.replace('\\', '/');
      name = path.substring(engine.getTemplatePath().length());
    }
    TemplateReader in = new TemplateReader(reader);
    doc = new Document(this, in);
    in.checkStatus(name);
    if (parse) {
      this.parser = toParser(null);
    }
  }

  /**
   * 根据文件的内容新建模板对象。
   * 
   * @param engine
   *          模板引擎
   * @param file
   *          文件描述对象
   * @throws IOException
   *           模板读写过程中产生错误
   * @throws TemplateException
   *           模板生成过程中产生错误
   */
  Template(Engine engine, File file) throws IOException, TemplateException {
    this(engine, file.getAbsolutePath(), new InputStreamReader(
        new FileInputStream(file), engine.getEncoding()), true);
    this.file = file;
    name = path.substring(engine.getTemplatePath().length());
    lastModified = file.lastModified();
  }

  /**
   * 获取模板引擎对象。
   * 
   * @return 模板引擎对象
   */
  public Engine getEngine() {
    return engine;
  }

  /**
   * 获取当前模板文件名。
   * 
   * @return 模板文件名
   */
  public String getName() {
    return name;
  }

  /**
   * 检测模板文件的更新情况。
   * 
   * @return <tt>true</tt>模板相关文件自上次编译后被更新;
   *         <tt>false</tt>模板相关文件自上次编译后没有更新.
   */
  public boolean isUpdated() {
    if (file.lastModified() > lastModified) {
      return true;
    }
    if (associatedFiles != null) {
      for (File file : associatedFiles) {
        if (file.lastModified() > lastModified) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 获得当前模板文件的地址。
   * 
   * @return 模板文件的地址
   */
  public String getPath() {
    return path;
  }

  /**
   * 获得相对于模板地址的地址，如果以'/'开头，将取得相对模板控制器根路径的地址，
   * 否则相对于当前模板地址转换。
   * 
   * @param path
   *          相对地址描述
   * @param isTemplateName
   *          <tt>true</tt>表示计算模板的名称;
   *          <tt>false</tt>表示计算绝对路径
   * @return 转换后的文件名
   * @see org.lilystudio.smarty4j.statement.function.$include
   */
  public String getPath(String path, boolean isTemplateName) {
    if (name == null) {
      return path;
    } else if (path.charAt(0) != '/') {
      // 取得当前模板文件的路径
      String s = isTemplateName ? name : this.path;
      int last = s.lastIndexOf('/');
      if (last >= 0) {
        path = s.substring(0, last + 1) + path;
      }
    } else if (!isTemplateName) {
      return engine.getTemplatePath() + path;
    }
    return path;
  }

  /**
   * 增加与模板文件相关联的文件。
   * 
   * @param file
   *          需要增加的文件描述对象
   */
  public void associate(File file) {
    if (associatedFiles == null) {
      associatedFiles = new ArrayList<File>();
    }
    lastModified = Math.max(file.lastModified(), lastModified);
    associatedFiles.add(file);
  }

  /**
   * 获取模板对象中指定的扩展节点。
   * 
   * @param index
   *          扩展节点的编号
   * @return 扩展节点
   */
  public INode getNode(int index) {
    return nodes.get(index);
  }

  /**
   * 往模板对象中新增扩展节点。
   * 
   * @param node
   *          扩展节点
   * @return 添加成功后扩展节点对应的序号
   */
  public int addNode(INode node) {
    if (nodes == null) {
      nodes = new ArrayList<INode>();
    }
    nodes.add(node);
    return nodes.size() - 1;
  }

  /**
   * 添加一次变量的使用记录。
   * 
   * @param name
   *          变量名
   */
  public void addUsedVariable(String name) {
    if (variables != null) {
      Boolean value = variables.get(name);
      if (value == null) {
        variables.put(name, Boolean.TRUE);
      }
    }
  }

  /**
   * 阻止指定的变量进行缓存。
   * 
   * @param name
   *          变量名称
   */
  public void preventCacheVariable(String name) {
    if (variables != null) {
      variables.put(name, Boolean.FALSE);
    }
  }

  /**
   * 阻止所有的变量进行缓存。
   */
  public void preventAllCache() {
    variables = null;
  }

  /**
   * 根据数据容器的内容解析模板，将结果输出到指定的二进制输出流。
   * 
   * @param context
   *          数据容器
   * @param out
   *          二进制输出流
   * @throws Exception
   *           如果合并执行时发生错误
   */
  public void merge(Context context, OutputStream out) throws Exception {
    Writer writer = new TemplateWriter(out, engine.getEncoding());
    try {
      merge(context, writer);
    } finally {
      writer.flush();
    }
  }

  /**
   * 根据数据容器的内容解析模板，将结果输出到指定的输出对象。
   * 
   * @param context
   *          数据容器
   * @param out
   *          输出对象
   */
  public void merge(Context context, Writer out) {
    context.setTemplate(this);
    parser.merge(context, out);
  }

  /**
   * 根据节点树创建模板解析类。
   * 
   * @param name
   *          类名
   * @return 转换器接口对象
   */
  public IParser toParser(String name) {
    ClassWriter cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
    if (name != null) {
      cw.visitSource(name, null);
    }
    MethodVisitor mw;
    name = "template";
    cw.visit(V1_5, ACC_PUBLIC, name, null, "java/lang/Object", INTERFACES);

    // 定义类的构造方法
    mw = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mw.visitVarInsn(ALOAD, THIS);
    mw.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
    mw.visitInsn(RETURN);
    mw.visitMaxs(0, 0);
    mw.visitEnd();

    // 定义类的merge方法
    mw = cw.visitMethod(ACC_PUBLIC, "merge", "(L" + Context.NAME
        + ";Ljava/io/Writer;)V", null, null);
    mw.visitVarInsn(ALOAD, CONTEXT);
    mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "getTemplate", "()L" + NAME
        + ";");
    mw.visitVarInsn(ASTORE, TEMPLATE);

    // 计算变量缓存占用的堆栈位置
    doc.scan(this);
    Map<String, Integer> variableNames = null;
    if (variables != null) {
      variableNames = new HashMap<String, Integer>();
      for (Entry<String, Boolean> variable : variables.entrySet()) {
        if (variable.getValue().booleanValue()) {
          int value = LOCAL_START + variableNames.size();
          String key = variable.getKey();
          mw.visitVarInsn(ALOAD, CONTEXT);
          mw.visitLdcInsn(key);
          mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
              "(Ljava/lang/String;)Ljava/lang/Object;");
          mw.visitVarInsn(ASTORE, value);
          variableNames.put(key, Integer.valueOf(value));
        }
      }
      variables = null;
    }

    doc.parse(mw, LOCAL_START
        + (variableNames != null ? variableNames.size() : 0), variableNames);

    mw.visitInsn(RETURN);
    mw.visitMaxs(0, 0);
    mw.visitEnd();
    cw.visitEnd();
    byte[] code = cw.toByteArray();
    try {
      return (IParser) DynamicClassLoader.getClass(name, code).newInstance();
    } catch (Exception e) {
      // 出现概率极低
      throw new RuntimeException("不能实例化Parser对象");
    }
  }
}