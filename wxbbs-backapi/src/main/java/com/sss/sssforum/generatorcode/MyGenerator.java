package com.sss.sssforum.generatorcode;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.List;


public class MyGenerator {

    private static String dbUrl =
            "jdbc:mysql://127.0.0.1:3306/sss-forum?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT"
                    + "&nullNamePatternMatchesAll=true";
    private static String dbUserName = "root";
    private static String dbPassword = "root123..";
    private static String dbDriverClassName = "com.mysql.jdbc.Driver";

    private static String outputDir = System.getProperty("user.dir") + "\\src\\main\\java";

    private static String packageName = "com.sss.sssforum.wxclient.file";

   // private static String[] tableNames = {"sys_job","sss_menu","sss_role_menu","sss_user_role"};
    private static String[] tableNames = {"file_info"};

    public static void main(String[] args) {
          generatorCode();
    }
    private static void generatorCode(){
        //mySql的数据类型转换
         MySqlTypeConvert mySqlTypeConvert=new MySqlTypeConvert(){
            @Override
            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                String s = fieldType.toLowerCase();
                if (s.contains("datetime") || s.contains("timestamp")) {
                    return DbColumnType.TIMESTAMP;
                }
                return super.processTypeConvert(globalConfig, fieldType);
            }
        };
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
       // String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(outputDir);
        gc.setAuthor("sss");
        //设置生成代码后是否打开代码文件目录
        gc.setOpen(true);
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        gc.setActiveRecord(false);
        gc.setFileOverride(true);
        gc.setServiceName("I%sService").setMapperName("I%sDao").setXmlName("I%sDao").setBaseResultMap(true).setBaseColumnList(true);
//        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setTypeConvert(mySqlTypeConvert);
        dsc.setUrl(dbUrl);
        // dsc.setSchemaName("public");
        dsc.setDriverName(dbDriverClassName);
        dsc.setUsername(dbUserName);
        dsc.setPassword(dbPassword);
//        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
       // pc.setModuleName("com.sss.common");
        pc.setEntity("entity").setController("controller").setService("service").setMapper("dao");
                //.setXml("dao");
        //设置entity，service,dao上级包
        pc.setParent(packageName);
//        mpg.setPackageInfo(pc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //设置要生成的表
        strategy.setInclude(tableNames);
        //设置自动填充字段
        TableFill createTime = new TableFill("create_time", FieldFill.INSERT);
        TableFill updateTime = new TableFill("update_time", FieldFill.UPDATE);
        ArrayList<TableFill> tableFills = new ArrayList<>();
        tableFills.add(createTime);
        tableFills.add(updateTime);
        //strategy.setTableFillList(tableFills);
        //设置逻辑删除字段
        strategy.setLogicDeleteFieldName("is_deleted");
//        strategy.setTablePrefix("");
        //设置entity，service,dao继承的父类
//        strategy.setSuperEntityClass("com.sss.sssforum.base.BaseEntity");
//        strategy.setSuperServiceClass("com.baomidou.ant.common.BaseService");
//        strategy.setSuperServiceImplClass("com.baomidou.ant.common.BaseServiceImpl");
//       strategy.setSuperMapperClass("com.baomidou.ant.common.BaseMapper");
//       strategy.setSuperControllerClass("com.baomidou.ant.common.BaseController");
        //是否使用lombook注解
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
//        mpg.setStrategy(strategy);
//       mpg.setTemplateEngine(new FreemarkerTemplateEngine());
//        mpg.execute();

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        // 如果模板引擎是 velocity
        String templatePath = "/templates/mapper.xml.vm";
        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                String s = System.getProperty("user.dir") + "/src/main/resources/mapper/" +tableInfo.getEntityName().toLowerCase()+"/"+"I"+tableInfo.getEntityName() + "Dao" + StringPool.DOT_XML;
                return s;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);
        mpg.setGlobalConfig(gc).setDataSource(dsc).setStrategy(strategy).setPackageInfo(pc);
        mpg.execute();
    }
}
