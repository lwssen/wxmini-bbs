package com.sss.sssforum;

import io.github.yedaxia.apidocs.Docs;
import io.github.yedaxia.apidocs.DocsConfig;

/**
 * @author lws
 * @date 2020-09-13 18:07
 **/
public class ApidocMain {
    public static void main(String[] args) {
        DocsConfig config = new DocsConfig();
        config.setProjectPath(System.getProperty("user.dir")); // root project path
        config.setProjectName("bbs-wep-api"); // project name
        config.setApiVersion("");       // api version
        config.setDocsPath(System.getProperty("user.dir")+"/src/main/resources/templates/apidocs/"); // api docs target path
        config.setAutoGenerate(Boolean.TRUE);  // auto generate
        System.out.println("ResourcePath资源路径"+config.getResourcePath());
        Docs.buildHtmlDocs(config); // execute to generate
    }
}
