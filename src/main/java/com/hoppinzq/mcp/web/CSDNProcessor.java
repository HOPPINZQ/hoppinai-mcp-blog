package com.hoppinzq.mcp.web;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

@Component
public class CSDNProcessor implements PageProcessor {

    @Override
    public void process(Page page) {
        Selectable links = page.getHtml().links();
        String blogTitle = page.getHtml().xpath("//*[@id=\"articleContentId\"]/html()").toString();
        String blogText = page.getHtml().xpath("//*[@id=\"content_views\"]/tidyText()").toString();
        String blogHtml = page.getHtml().xpath("//*[@id=\"content_views\"]/html()").toString();
        String blogAuthor = page.getHtml().xpath("//*[@id=\"mainBox\"]/main/div[1]/div[1]/div/div[2]/div[1]/div/a[1]/html()").toString();
        String imageSrc = page.getHtml().xpath("//*[@id=\"mainBox\"]/main/div[1]/div[1]/div/div[2]/div[1]/img/@src").toString();
        List<String> blogCategory = new ArrayList<String>();
        int classCategory = 2;
        while (true) {
            String xpathCategoryA = "//*[@id=\"mainBox\"]/main/div[1]/div[1]/div/div[2]/div[2]/div/a[" + classCategory + "]/text()";
            String blogCategoryText = page.getHtml().xpath(xpathCategoryA).toString();
            if (blogCategoryText != null) {
                blogCategory.add(blogCategoryText);
                classCategory++;
            } else {
                break;
            }
        }
        String blogDate = page.getHtml().xpath("//*[@id=\"mainBox\"]/main/div[1]/div[1]/div/div[2]/div[1]/div/span[2]/html()").toString();
        ObjectNode response = (ObjectNode) WebMessageContext.getPrincipal();
        if (response != null) {
            response.put("blogTitle", blogTitle);
            response.put("blogCategory", blogCategory.toString());
            response.put("blogText", blogText);
            response.put("blogAuthor", blogAuthor);
            response.put("image", imageSrc);
            response.put("blogDate", blogDate);
        }
    }

    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(3).setSleepTime(1000);
    }
}
