package com.open.configs.web;

import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.VelocityResult;
import org.apache.struts2.views.JspSupportServlet;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;

public class VelocityLayoutResult extends VelocityResult
{

    private static final long serialVersionUID = 1L;

    private static final Log LOG = LogFactory.getLog(VelocityResult.class);

    private VelocityManager velocityManager;

    // location where to load the layout template
    protected String layoutDir;

    // the default layout template to be loaded
    protected String defaultLayout;

    // the default error template to be loaded
    protected String errorTemplate;

    /**
     * The default layout directory
     */
    public static final String DEFAULT_LAYOUT_DIR = "layout/";

    /**
     * The default filename for the servlet's default layout
     */
    public static final String DEFAULT_DEFAULT_LAYOUT = "Default.vm";

    /**
     * The default error template's filename.
     */
    public static final String DEFAULT_ERROR_TEMPLATE = "Error.vm";

    /**
     * The velocity.properties key for specifying the servlet's error template.
     */
    public static final String PROPERTY_ERROR_TEMPLATE = "tools.view.servlet.error.template";

    /**
     * The velocity.properties key for specifying the relative directory holding
     * layout templates.
     */
    public static final String PROPERTY_LAYOUT_DIR = "tools.view.servlet.layout.directory";

    /**
     * The velocity.properties key for specifying the servlet's default layout
     * template's filename.
     */
    public static final String PROPERTY_DEFAULT_LAYOUT = "tools.view.servlet.layout.default.template";

    public VelocityLayoutResult()
    {
        super();
        LOG.debug("DEBUG: creat VeloctiyLayoutResult....");
    }

    /**
     * The context key that will hold the content of the screen. This key
     * ($screen_content) must be present in the layout template for the current
     * screen to be rendered.
     */
    public static final String KEY_SCREEN_CONTENT = "screen_content";

    /**
     * The context/parameter key used to specify an alternate layout to be used
     * for a request instead of the default layout.
     */
    public static final String KEY_LAYOUT = "layout";

    @Inject
    public void setVelocityManager(VelocityManager velocityManager)
    {
        this.velocityManager = velocityManager;
    }

    @Override
    public void doExecute(String finalLocation, ActionInvocation invocation)
            throws Exception
    {
        // generator turly useful content
        this.generatorContent(finalLocation, invocation);

    }

    private void generatorContent(String finalLocation,
                                  ActionInvocation invocation) throws Exception
    {
        long runtime = System.currentTimeMillis();

        ValueStack stack = ActionContext.getContext().getValueStack();

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        JspFactory jspFactory = null;
        ServletContext servletContext = ServletActionContext
                .getServletContext();
        Servlet servlet = JspSupportServlet.jspSupportServlet;

        velocityManager.init(servletContext);

        boolean usedJspFactory = false;
        PageContext pageContext = (PageContext) ActionContext.getContext().get(
                ServletActionContext.PAGE_CONTEXT);

        if (pageContext == null && servlet != null)
        {
            jspFactory = JspFactory.getDefaultFactory();
            pageContext = jspFactory.getPageContext(servlet, request, response,
                    null, true, 8192, true);
            ActionContext.getContext().put(ServletActionContext.PAGE_CONTEXT,
                    pageContext);
            usedJspFactory = true;
        }

        try
        {
            // ------------- 1. render the screen template ------------
            String encoding = getEncoding(finalLocation);
            String contentType = getContentType(finalLocation);

            if (encoding != null)
            {
                contentType = contentType + ";charset=" + encoding;
            }
            Template t = getTemplate(stack,
                    velocityManager.getVelocityEngine(), invocation,
                    finalLocation, encoding);
            Context context = createContext(velocityManager, stack, request,
                    response, finalLocation);
            Writer screenWriter = new StringWriter();
            response.setContentType(contentType);
            t.merge(context, screenWriter);
            context.put(KEY_SCREEN_CONTENT, screenWriter.toString());

            // ------------- 2. render the layout template -------------

            initLayoutTemplateParameters();

            String layout = getLayoutTemplate(context);

            try
            {
                // load the layout template
                t = getTemplate(stack, velocityManager.getVelocityEngine(),
                        invocation, layout, encoding);
            }
            catch (Exception e)
            {

                // if it was an alternate layout we couldn't get...
                if (!layout.equals(defaultLayout))
                {
                    // try to get the default layout
                    // if this also fails, let the exception go
                    t = getTemplate(stack, velocityManager.getVelocityEngine(),
                            invocation, defaultLayout, encoding);
                }
            }

            Writer writer = new OutputStreamWriter(response.getOutputStream(),
                    encoding);
            // Render the layout template into the response
            t.merge(context, writer);

            // generator exec template time
            Date cur_time = Calendar.getInstance(
                    invocation.getInvocationContext().getLocale()).getTime();
            writer.write("\r\n<!-- Generated by EATELE.NET (");
            writer.write(cur_time + "");
            writer.write(") ");
            writer.write((cur_time.getTime() - runtime) + "");
            writer.write("ms -->");
            // always flush the writer (we used to only flush it if this was a
            // jspWriter, but someone asked
            // to do it all the time (WW-829). Since Velocity support is being
            // deprecated, we'll oblige :)
            writer.flush();

        }
        catch (Exception e)
        {
            LOG.error("Unable to render Velocity Template, '" + finalLocation
                    + "'", e);
            throw e;
        }
        finally
        {
            if (usedJspFactory)
            {
                jspFactory.releasePageContext(pageContext);
            }
        }

        return;
    }

    // init default layout properties param
    private void initLayoutTemplateParameters()
    {
        if (layoutDir == null)
        {
            // only for initialize the layoutDir
            layoutDir = (String) velocityManager.getVelocityEngine()
                    .getProperty(PROPERTY_LAYOUT_DIR);
            if (layoutDir == null || layoutDir.length() == 0)
            {
                layoutDir = DEFAULT_LAYOUT_DIR;
            }
        }
        if (defaultLayout == null)
        {
            // only for initialize the defaultLayout
            defaultLayout = (String) velocityManager.getVelocityEngine()
                    .getProperty(PROPERTY_DEFAULT_LAYOUT);
            if (defaultLayout == null || defaultLayout.length() == 0)
            {
                defaultLayout = DEFAULT_DEFAULT_LAYOUT;
            }
            defaultLayout = layoutDir + defaultLayout;
        }

        if (errorTemplate == null)
        {
            errorTemplate = (String) velocityManager.getVelocityEngine()
                    .getProperty(PROPERTY_ERROR_TEMPLATE);
            if (errorTemplate == null || errorTemplate.length() == 0)
            {
                defaultLayout = DEFAULT_ERROR_TEMPLATE;
            }
        }
        LOG.info("VelocityLayoutResult: Error screen is '" + errorTemplate
                + "'");
        LOG.info("VelocityLayoutResult: Layout directory is '" + layoutDir
                + "'");
        LOG.info("VelocityLayoutResult: Default layout template is '"
                + defaultLayout + "'");
    }

    protected String getLayoutTemplate(Context context)
    {
        Object obj = context.get(KEY_LAYOUT);
        String layout = (obj == null) ? null : obj.toString();
        if (layout == null)
        {
            // no alternate, use default
            layout = defaultLayout;
        }
        else
        {
            // make it a full(er) path
            layout = layoutDir + layout;
        }
        return layout;
    }

    /**
     * Overrides to display user's custom error template
     */
    protected void error(HttpServletRequest request,
                         HttpServletResponse response, Throwable e)
    {

    }
}
