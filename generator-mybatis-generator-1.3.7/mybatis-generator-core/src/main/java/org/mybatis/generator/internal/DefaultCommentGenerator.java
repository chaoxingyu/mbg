/**
 *    Copyright 2006-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.internal;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.util.StringUtility;

/** @author Jeff Butler */
public class DefaultCommentGenerator implements CommentGenerator {

  /** 配置文件. */
  private Properties properties;

  /** 父类的时间. */
  private boolean suppressDate;

  /** 父类的所有注释. */
  private boolean suppressAllComments;

  /**
   * 添加表格的注释。如果suppressAllComments为true，则忽略此选项 If suppressAllComments is true, this option is
   * ignored.
   */
  private boolean addRemarkComments;

  /** 文件版权说明 TODO 待完善 */
  private boolean addJavaFileComment;

  /** 时间格式 * */
  private SimpleDateFormat dateFormat;

  public DefaultCommentGenerator() {
    super();
    properties = new Properties();
    suppressDate = false;
    suppressAllComments = false;
    addRemarkComments = false;
    addJavaFileComment = false;
  }

  /**
   * 判断非NULL与非空
   *
   * @param str str
   * @return boolean
   */
  private boolean isNotBlank(String str) {
    return !(null == str || str.length() == 0 || str.isEmpty());
  }

  /**
   * 给Java文件加注释，生成版权等信息，这个注释是在文件的顶部，也就是package上面。。
   *
   * @param compilationUnit compilationUnit
   */
  @Override
  public void addJavaFileComment(CompilationUnit compilationUnit) {
    if (addJavaFileComment) {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String dateStr = dateFormat.format(new Date());
      compilationUnit.addFileCommentLine("/** ");
      compilationUnit.addFileCommentLine(
          " * " + compilationUnit.getType().getShortName() + ".java");
      compilationUnit.addFileCommentLine(
          " * Copyright 2010-" + dateStr.substring(0, 4) + " the MBG. All Rights Reserved.");
      compilationUnit.addFileCommentLine(" * generated by MBG");
      compilationUnit.addFileCommentLine(" * generated on " + dateStr);
      compilationUnit.addFileCommentLine(" */ ");
    }
  }

  /**
   * Mybatis的Mapper.xml文件里面的注释
   *
   * <p>Adds a suitable comment to warn users that the element was generated, and when it was
   * generated.
   *
   * @param xmlElement the xml element
   */
  @Override
  public void addComment(XmlElement xmlElement) {
    if (suppressAllComments) {
      return;
    }
  }

  /**
   * 调用此方法为根元素的第一个子节点添加注释。 此方法可用于添加 一般文件注释（如版权声明）。 但是，请注意，XML文件合并功能不会处理 这个注释。 如果反复运行生成器，则只保留初始运行的注释。
   */
  @Override
  public void addRootComment(XmlElement rootElement) {
    // add no document level comments by default
  }

  /**
   * 从properties配置文件中添加此实例的属性
   *
   * @param properties properties
   */
  @Override
  public void addConfigurationProperties(Properties properties) {
    this.properties.putAll(properties);

    suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));

    suppressAllComments =
        isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));

    addRemarkComments =
        isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS));

    String dateFormatString =
        properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_DATE_FORMAT);
    if (StringUtility.stringHasValue(dateFormatString)) {
      dateFormat = new SimpleDateFormat(dateFormatString);
    }
  }

  /**
   * 此方法用于添加自定义javadoc标签。 如果您不希望包含Javadoc标签，您可能不会执行任何操作 - *
   * 但是，如果不包含Javadoc标签，那么eclipse插件的Java合并功能就会中断。
   *
   * <p>This method adds the custom javadoc tag for. You may do nothing if you do not wish to
   * include the Javadoc tag - however, if you do not include the Javadoc tag then the Java merge
   * capability of the eclipse plugin will break.
   *
   * @param javaElement the java element
   * @param markAsDoNotDelete the mark as do not delete
   */
  protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
    javaElement.addJavaDocLine(" *"); // $NON-NLS-1$
    StringBuilder sb = new StringBuilder();
    sb.append(" * "); // $NON-NLS-1$
    sb.append(MergeConstants.NEW_ELEMENT_TAG);
    if (markAsDoNotDelete) {
      sb.append(" do_not_delete_during_merge"); // $NON-NLS-1$
    }
    String s = getDateString();
    if (s != null) {
      sb.append(' ');
      sb.append(s);
    }
    javaElement.addJavaDocLine(sb.toString());
  }

  /**
   * Returns a formated date string to include in the Javadoc tag and XML comments. You may return
   * null if you do not want the date in these documentation elements.
   *
   * @return a string representing the current timestamp, or null
   */
  protected String getDateString() {
    if (suppressDate) {
      return null;
    } else if (dateFormat != null) {
      return dateFormat.format(new Date());
    } else {
      return new Date().toString();
    }
  }

  /** 类注释 */
  @Override
  public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
    if (suppressAllComments) {
      return;
    }
    innerClass.addJavaDocLine("/**");
    innerClass.addJavaDocLine(" * @Title " + introspectedTable.getFullyQualifiedTable() + "表的实体类");
    String remark = introspectedTable.getRemarks();
    if (isNotBlank(remark)) {
      innerClass.addJavaDocLine(" * @Description " + remark);
    }
    innerClass.addJavaDocLine(" * @Author generated by MBG");
    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    innerClass.addJavaDocLine(" * @Date " + dateFormat.format(new Date()));
    innerClass.addJavaDocLine(" * @version 1.0.0");
    innerClass.addJavaDocLine(" * ");
    innerClass.addJavaDocLine(" */");
  }

  @Override
  public void addClassComment(
      InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
    this.addClassComment(innerClass, introspectedTable);
  }

  /** 实体类注释 为模型类添加注释。 Java代码合并应该 通知不要删除整个class，万一有任何class 已经做出了改变。 所以这个方法会永远使用“不要删除”注释。 */
  @Override
  public void addModelClassComment(
      TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    if (suppressAllComments) {
      return;
    }
    topLevelClass.addJavaDocLine("/**");
    topLevelClass.addJavaDocLine(" * ");
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("表：");
    TableConfiguration tableConfiguration = introspectedTable.getTableConfiguration();
    stringBuilder.append(tableConfiguration.getTableName());
    stringBuilder.append(" ");
    String remark = introspectedTable.getRemarks();
    if (isNotBlank(remark)) {
      stringBuilder.append(remark);
    }
    stringBuilder.append(" 的实体类");
    topLevelClass.addJavaDocLine(" * " + stringBuilder.toString());
    topLevelClass.addJavaDocLine(" * ");
    topLevelClass.addJavaDocLine(" * " + "@author  generated by MBG");
    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    topLevelClass.addJavaDocLine(" * " + "@date " + dateFormat.format(new Date()));
    topLevelClass.addJavaDocLine(" * @version 1.0.0");
    topLevelClass.addJavaDocLine(" * ");
    topLevelClass.addJavaDocLine(" */");
  }

  @Override
  public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
    if (suppressAllComments) {
      return;
    }
  }

  @Override
  public void addFieldComment(
      Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
    if (suppressAllComments) {
      return;
    }
    field.addJavaDocLine("/**");
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" * ");
    stringBuilder.append("@Fields ");
    // 原始表字段
    stringBuilder.append(introspectedColumn.getActualColumnName());
    stringBuilder.append(" ");
    stringBuilder.append(field.getName());
    stringBuilder.append(" ");
    String remark = introspectedColumn.getRemarks();
    if (isNotBlank(remark)) {
      stringBuilder.append(remark);
    }
    field.addJavaDocLine(stringBuilder.toString());
    field.addJavaDocLine(" */");
  }

  @Override
  public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
    if (suppressAllComments) {
      return;
    }
    field.addJavaDocLine("/**  表： " + introspectedTable.getFullyQualifiedTable() + "  */");
  }

  /** 普通方法注释,mapper接口中方法 */
  @Override
  public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
    if (suppressAllComments) {
      return;
    }
    method.addJavaDocLine("/**");
    StringBuilder sb = new StringBuilder();
    sb.append(" * ");
    if (method.isConstructor()) {
      sb.append(" 构造查询条件 ");
    }
    String methodName = method.getName();
    if ("toString".equals(methodName)) {
      sb.append(" toString");
    } else if ("selectAll".equals(methodName)) {
      sb.append(" 查询全部");
    } else if ("setOrderByClause".equals(methodName)) {
      sb.append(" 设置排序字段");
    } else if ("setDistinct".equals(methodName)) {
      sb.append(" 设置过滤重复数据");
    } else if ("getOredCriteria".equals(methodName)) {
      sb.append(" 获取当前的查询条件实例");
    } else if ("isDistinct".equals(methodName)) {
      sb.append(" 是否过滤重复数据");
    } else if ("getOrderByClause".equals(methodName)) {
      sb.append(" 获取排序字段");
    } else if ("createCriteria".equals(methodName)) {
      sb.append(" 创建一个查询条件");
    } else if ("createCriteriaInternal".equals(methodName)) {
      sb.append(" 内部构建查询条件对象");
    } else if ("clear".equals(methodName)) {
      sb.append(" 清除查询条件");
    } else if ("countByExample".equals(methodName)) {
      sb.append(" 根据指定的条件获取数据库记录数");
    } else if ("deleteByExample".equals(methodName)) {
      sb.append(" 根据指定的条件删除数据库符合条件的记录");
    } else if ("deleteByPrimaryKey".equals(methodName)) {
      sb.append(" 根据主键删除数据库的记录");
    } else if ("insert".equals(methodName)) {
      sb.append(" 新写入数据库记录");
    } else if ("insertSelective".equals(methodName)) {
      sb.append(" 动态字段,写入数据库记录");
    } else if ("selectByExample".equals(methodName)) {
      sb.append(" 根据指定的条件查询符合条件的数据库记录");
    } else if ("selectByPrimaryKey".equals(methodName)) {
      sb.append(" 根据指定主键获取一条数据库记录");
    } else if ("updateByExampleSelective".equals(methodName)) {
      sb.append(" 动态根据指定的条件来更新符合条件的数据库记录");
    } else if ("updateByExample".equals(methodName)) {
      sb.append(" 根据指定的条件来更新符合条件的数据库记录");
    } else if ("updateByPrimaryKeySelective".equals(methodName)) {
      sb.append(" 动态字段,根据主键来更新符合条件的数据库记录");
    } else if ("updateByPrimaryKey".equals(methodName)) {
      sb.append(" 根据主键来更新符合条件的数据库记录");
    }
    final List<Parameter> parameterList = method.getParameters();
    boolean isHaveParameter = true;
    if (null == parameterList || parameterList.isEmpty()) {
      isHaveParameter = false;
      if ("or".equals(methodName)) {
        sb.append(" 创建一个新的或者查询条件");
      }
    } else {
      method.addJavaDocLine(" *");
      if ("or".equals(methodName)) {
        sb.append(" 增加或者的查询条件,用于构建或者查询");
      }
    }
    sb.append(" : ");
    sb.append(introspectedTable.getFullyQualifiedTable());
    method.addJavaDocLine(sb.toString());
    if (isHaveParameter) {
      for (Parameter parameter : parameterList) {
        sb.setLength(0);
        sb.append(" * @param "); // $NON-NLS-1$
        String paramterName = parameter.getName();
        sb.append(paramterName);
        if ("orderByClause".equals(paramterName)) {
          sb.append(" 排序字段"); // $NON-NLS-1$
        } else if ("distinct".equals(paramterName)) {
          sb.append(" 是否过滤重复数据");
        } else if ("criteria".equals(paramterName)) {
          sb.append(" 过滤条件实例");
        } else {
          sb.append(" ");
          sb.append(paramterName);
        }
        method.addJavaDocLine(sb.toString());
      }
    }
    FullyQualifiedJavaType fullyQualifiedJavaType = method.getReturnType();
    if (null != fullyQualifiedJavaType) {
      String returnType = fullyQualifiedJavaType.toString();
      if (returnType.contains("List")) {
        returnType = "List";
      } else {
        int lastIndex = returnType.lastIndexOf(".");
        returnType = lastIndex != -1 ? returnType.substring(lastIndex + 1) : returnType;
      }
      method.addJavaDocLine(" * @return " + returnType);
    }
    method.addJavaDocLine(" */");
  }

  @Override
  public void addGetterComment(
      Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
    if (suppressAllComments) {
      return;
    }
    // method.addJavaDocLine("/**   "+introspectedColumn.getRemarks()+"  "+
    // introspectedColumn.getActualColumnName() +"   **/");
  }

  @Override
  public void addSetterComment(
      Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
    if (suppressAllComments) {
      return;
    }
    // method.addJavaDocLine("/**   "+introspectedColumn.getRemarks()+"  "+
    // introspectedColumn.getActualColumnName() +"   **/");
  }

  @Override
  public void addGeneralMethodAnnotation(
      Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
    imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); // $NON-NLS-1$
    String comment =
        "Source Table: " + introspectedTable.getFullyQualifiedTable().toString(); // $NON-NLS-1$
    method.addAnnotation(getGeneratedAnnotation(comment));
  }

  @Override
  public void addGeneralMethodAnnotation(
      Method method,
      IntrospectedTable introspectedTable,
      IntrospectedColumn introspectedColumn,
      Set<FullyQualifiedJavaType> imports) {
    imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); // $NON-NLS-1$
    String comment =
        "Source field: " //$NON-NLS-1$
            + introspectedTable.getFullyQualifiedTable().toString()
            + "." //$NON-NLS-1$
            + introspectedColumn.getActualColumnName();
    method.addAnnotation(getGeneratedAnnotation(comment));
  }

  @Override
  public void addFieldAnnotation(
      Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
    imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); // $NON-NLS-1$
    String comment =
        "Source Table: " + introspectedTable.getFullyQualifiedTable().toString(); // $NON-NLS-1$
    field.addAnnotation(getGeneratedAnnotation(comment));
  }

  @Override
  public void addFieldAnnotation(
      Field field,
      IntrospectedTable introspectedTable,
      IntrospectedColumn introspectedColumn,
      Set<FullyQualifiedJavaType> imports) {
    imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); // $NON-NLS-1$
    String comment =
        "Source field: " //$NON-NLS-1$
            + introspectedTable.getFullyQualifiedTable().toString()
            + "." //$NON-NLS-1$
            + introspectedColumn.getActualColumnName();
    field.addAnnotation(getGeneratedAnnotation(comment));

    if (!suppressAllComments && addRemarkComments) {
      String remarks = introspectedColumn.getRemarks();
      if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
        field.addJavaDocLine("/**"); // $NON-NLS-1$
        field.addJavaDocLine(" * Database Column Remarks:"); // $NON-NLS-1$
        String[] remarkLines = remarks.split(System.getProperty("line.separator")); // $NON-NLS-1$
        for (String remarkLine : remarkLines) {
          field.addJavaDocLine(" *   " + remarkLine); // $NON-NLS-1$
        }
        field.addJavaDocLine(" */"); // $NON-NLS-1$
      }
    }
  }

  @Override
  public void addClassAnnotation(
      InnerClass innerClass,
      IntrospectedTable introspectedTable,
      Set<FullyQualifiedJavaType> imports) {
    imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); // $NON-NLS-1$
    String comment =
        "Source Table: " + introspectedTable.getFullyQualifiedTable().toString(); // $NON-NLS-1$
    innerClass.addAnnotation(getGeneratedAnnotation(comment));
  }

  private String getGeneratedAnnotation(String comment) {
    StringBuilder buffer = new StringBuilder();
    buffer.append("@Generated("); // $NON-NLS-1$
    if (suppressAllComments) {
      buffer.append('\"');
    } else {
      buffer.append("value=\""); // $NON-NLS-1$
    }

    buffer.append(MyBatisGenerator.class.getName());
    buffer.append('\"');

    if (!suppressDate && !suppressAllComments) {
      buffer.append(", date=\""); // $NON-NLS-1$
      buffer.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()));
      buffer.append('\"');
    }

    if (!suppressAllComments) {
      buffer.append(", comments=\""); // $NON-NLS-1$
      buffer.append(comment);
      buffer.append('\"');
    }

    buffer.append(')');
    return buffer.toString();
  }
}
