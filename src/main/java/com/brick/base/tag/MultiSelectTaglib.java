package com.brick.base.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import com.brick.log.service.LogPrint;

public class MultiSelectTaglib extends TagSupport 
{
    //下拉框数据,初始化下拉框的数据
	private String disabled;
	private List<LabelValueBean> items;
    //名称参数,传到后台的名称
    private String nameParams;
    //id参数，传到后台的id
    private String idParams;
    //当前id的值，主要用于查询后再次选中查询前的checkbox
    private String curIdVal;
    //样式，设置文本框样式
    private String textStyle;
    //是否只读(可选)，设置文本框只读属性，默认只读
    private boolean readonly = true;
    //是否需要放大镜(可选)，设置是否有放大镜图标，默认有放大镜图标
    //下拉框高度(可选)，设置高度后，超过高度后会显示下拉框
    private String selectHeight; 
    //下拉框宽度(可选)，设置宽度后，超过高度后会显示下拉框,默认200px
    private String selectWidth;
    //是否需要全选功能(可选)
    private boolean hasAllSelector;
    
    //文本框的值
    String strInputValue;
    
    //国际化用到的参数
    private final String SELECT_ALL = "select_all";
    private final String RESOURCE_NAME = "ssb_application_resource";
    /**
     * 重写doStartTag
     */
    public int doStartTag() 
    {        
        //得到资源文件
    	
        Locale locale =  pageContext.getRequest().getLocale();
       
        //ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_NAME, locale);
        Map<String, String> messages = new HashMap<String, String>();
        messages.put(SELECT_ALL, "全选");
    	
        JspWriter out = pageContext.getOut();
        
        if (out != null) 
        {
            try 
            {    
                out.print(getContentString(messages));
            } catch (IOException e) 
            {     
            	e.printStackTrace();
            }
        }

        return SKIP_BODY;
    }
    
    /**
     * 拼接显示的多选框域
     * @return
     */
    private String getContentString(Map<String, String> messages)
    {
        //用户存放HTML内容串
        StringBuffer contentStr = new StringBuffer() ;
       //如果没设置宽度，默认200px
        selectWidth = "150px";
        contentStr.append("<div class=\"multiSelectDiv\">");
        contentStr.append("<input id=\"issureListID\" type=\"text\" name=\"").append(nameParams).append("\"");
        contentStr.append("  value=\""+strInputValue+"\"");
        //如果需要把文本框设置为只读
        if(readonly)
        {
            contentStr.append("readonly=\"readonly\"");
        }
        
        if("disabled".equals(disabled))
        {
            contentStr.append("disabled=\"disabled\"");
        }
        
        //文本框默认样式
        String textDefStyle = "width:150px";
        contentStr.append(" style=\"").append( textDefStyle ).append("\"/>")
                  .append("<input type=\"hidden\" name=\"").append(idParams).append("\" value=\"" + curIdVal + "\"/>");
        
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
      
        
        /*
         * 如果以后要支持自定义其他样式，可以将这里的高度和宽度去掉， 直接自定义下拉框的样式，
         * 注意这里默认的"display:none;position: absolute;"是必需的样式，如果自定义里有同样属性定义，会直接覆盖同名属性的值.
         * 这里为了使用简便，暂时不支持
         String selectDefStyle = "display:none;position: absolute;";        
        */
        
        contentStr.append("<div style=\"display:none;position: absolute;margin-left: 60px;"); 
        //如果设置了下拉框高度
        contentStr.append("height:" + selectHeight + ";");
        contentStr.append("width:" + selectWidth + ";");
        contentStr.append("\">");
        contentStr.append("   <table  cellspacing=\"1\" class=\"tb_datalist\" bgcolor=\"#cccccc\">");
        //如果list不为空，生成下拉列表
        if(items.isEmpty())
        {
            contentStr.append("<tr><td><td></tr>");
        }
        else
        {
            //如果需要全选功能
            if(hasAllSelector)
            {
                contentStr.append("<tr class=\"tr_title\">");
                contentStr.append("<td width=\"10%\">")
                .append("   <input class=\"selectAll\" value=\"\" type=\"checkbox\"/></td>");
                contentStr.append("<td width=\"90%\"><span>").append(messages.get(SELECT_ALL)).append("</span></td>");
                contentStr.append("</tr>");
            }
            //生成下拉列表
            for(LabelValueBean labelVal:items)
            {
                if(labelVal != null)
                {
                    contentStr.append("<tr>");
                    contentStr.append("<td width=\"10%\">")
                    .append("   <input type=\"checkbox\"  value=\"" + labelVal .getValue()+ "\"/></td>");
                    contentStr.append("<td width=\"90%\">").append("<span>" + labelVal.getLabel() + "</span></td>");
                    contentStr.append("</tr>");
                }
            }
        }
        contentStr.append("</table>");
        contentStr.append("</div>");
      
        return contentStr.toString() ;
    }


    public void release() {
        super.release();
    }

    public void setNameParams(String nameParams)
    {
        this.nameParams = nameParams;
    }

    public void setStyle(String textStyle)
    {
        this.textStyle = textStyle;
    }

    public void setIdParams(String idParams)
    {
        this.idParams = idParams;
    }

    public void setCurIdVal(String curIdVal)
    {
        this.curIdVal = curIdVal;
    }

    public void setItems(List<LabelValueBean> items)
    {
        this.items = items;
    }

      public void setSelectWidth(String selectWidth)
    {
        this.selectWidth = selectWidth;
    }

    public void setSelectHeight(String selectHeight)
    {
        this.selectHeight = selectHeight;
    }

    public void setHasAllSelector(boolean hasAllSelector)
    {
        this.hasAllSelector = hasAllSelector;
    }
    
    public void setdisabled(String disabled)
    {
        this.disabled = disabled;
    }
    
    public void setStrInputValue(String strInputValue)
    {
        this.strInputValue = strInputValue;
    }
    
}
