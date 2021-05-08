import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.EvaluationListener
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.ReadContext
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import org.testng.Assert
import org.testng.annotations.Test

/**
 * 案例：http://www.massapi.com/package/com/jayway/jsonpath/index.html
 */
class JsonPathTester {
    @Test
    void test01(){
        /**
         * 直接使用静态方法API进行调用,
         * 如果需要对同一个json解析多次，不建议使用，因为每次read都会重新解析一次json
         * 针对此种情况，建议使用ReadContext、WriteContext
         */
        String jsonString = "{ \"store\": { \"book\": [ { \"category\": \"reference\", \"author\": \"Nigel Rees\", \"title\": \"Sayings of the Century\", \"price\": 8.95 }, { \"category\": \"fiction\", \"author\": \"Evelyn Waugh\", \"title\": \"Sword of Honour\", \"price\": 12.99 }, { \"category\": \"fiction\", \"author\": \"Herman Melville\", \"title\": \"Moby Dick\", \"isbn\": \"0-553-21311-3\", \"price\": 8.99 }, { \"category\": \"fiction\", \"author\": \"J. R. R. Tolkien\", \"title\": \"The Lord of the Rings\", \"isbn\": \"0-395-19395-8\", \"price\": 22.99 } ], \"bicycle\": { \"color\": \"red\", \"price\": 19.95 } }, \"expensive\": 10 }"
        List authors = JsonPath.read(jsonString, '$.store.book[*].author')
        println authors
    }
    @Test
    void test02() {
        /**
         * 如果你只想读取一次，那么上面的代码就可以了
         * 如果你还想读取其他路径，现在上面不是很好的方法，因为他每次获取都需要再解析整个文档。
         * 所以，我们可以先解析整个文档，再选择调用路径。
         * 针对此种情况，建议使用ReadContext、WriteContext（jsonpath的上下文）
         *
         * ReadContext：返回一个Configuration用于读取
         * Configuration：用于读取JsonProvider对象
         * JsonProvider：解析给定的json字符串
         */
        String jsonString = "{ \"store\": { \"book\": [ { \"category\": \"reference\", \"author\": \"Nigel Rees\", \"title\": \"Sayings of the Century\", \"price\": 8.95 }, { \"category\": \"fiction\", \"author\": \"Evelyn Waugh\", \"title\": \"Sword of Honour\", \"price\": 12.99 }, { \"category\": \"fiction\", \"author\": \"Herman Melville\", \"title\": \"Moby Dick\", \"isbn\": \"0-553-21311-3\", \"price\": 8.99 }, { \"category\": \"fiction\", \"author\": \"J. R. R. Tolkien\", \"title\": \"The Lord of the Rings\", \"isbn\": \"0-395-19395-8\", \"price\": 22.99 } ], \"bicycle\": { \"color\": \"red\", \"price\": 19.95 } }, \"expensive\": 10 }"
        ReadContext ctx = JsonPath.parse(jsonString)//JsonContext对象
        println "返回正在运行的这个上下文（ctx）的JSON模型:\n" + ctx.json()//LinkedHashMap
        println "用字符串方式返回正在运行的这个上下文（ctx）的JSON模型:\n" + ctx.jsonString()//String
        List authorOfBookWithISBN = ctx.read('$.store.book[?(@.isbn)].author')
        println "从这个上下文读取给定的路径:\n" + authorOfBookWithISBN
        println "当达到maxresult限制时停止评估:\n" + ctx.limit(1).read('$.store.book[?(@.isbn)].author')

        /**
         * 通过ConfigurationBuilder（建造者方法）配置创建Configuration
         */
//        Object configuration = Configuration.builder().options()
//        Object configuration = Configuration.builder().evaluationListener()
//        Object configuration = Configuration.builder().jsonProvider()
//        Object configuration = Configuration.builder().mappingProvider()
        Object configuration = Configuration.builder().build()//默认值
//        println "通过ConfigurationBuilder创建Configuration指定的倾听者：" + configuration.evaluationListeners
//        println "通过ConfigurationBuilder创建Configuration指定的jsonProvider" + configuration.jsonProvider
//        println "通过ConfigurationBuilder创建Configuration指定的mappingProvider" + configuration.mappingProvider
//        println "通过ConfigurationBuilder创建Configuration指定的选项：" + configuration.options

//        Object listener = configuration.setEvaluationListeners('fuck' as EvaluationListener)
//        println "倾听者对这条路径的评价:" + ctx.withListeners(listener).read('$.store.book[?(@.isbn)].author')
        List<Map<String, Object>> expensiveBooks = JsonPath
                .using(configuration)
                .parse(jsonString)
                .read('$.store.book[?(@.price > 10)]', List.class)
        println expensiveBooks

        //  2.静态方法defaultConfiguration()默认配置创建
        String json = "{ \"store\": { \"book\": [ { \"category\": \"reference\", \"author\": \"Nigel Rees\", \"title\": \"Sayings of the Century\", \"price\": 8.95 }, { \"category\": \"fiction\", \"author\": \"Evelyn Waugh\", \"title\": \"Sword of Honour\", \"price\": 12.99 }, { \"category\": \"fiction\", \"author\": \"Herman Melville\", \"title\": \"Moby Dick\", \"isbn\": \"0-553-21311-3\", \"price\": 8.99 }, { \"category\": \"fiction\", \"author\": \"J. R. R. Tolkien\", \"title\": \"The Lord of the Rings\", \"isbn\": \"0-395-19395-8\", \"price\": 22.99 } ], \"bicycle\": { \"color\": \"red\", \"price\": 19.95 } }, \"expensive\": 10 }"
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json)
        println "根据默认值创建新的配置:\n" + document

        String author0 = JsonPath.read(document, '$.store.book[0].author')
        println 'author0：\n' + author0
        String author1 = JsonPath.read(document, '$.store.book[1].author')
        println 'author1：\n' + author1
    }
    @Test
    void test03() {
        /**
         * MappingProvider SPI反序列化器：
         *
         * 通常read后的返回值会进行自动转型到指定的类型，对应明确定义definite的表达式，
         * 应指定其对应的类型，对于indefinite含糊表达式，例如包括..、?()、[, (, )]，通常应该使用数组。
         *
         * 如果需要转换成具体的类型，则需要通过configuration配置mappingprovider
         * 其中JsonSmartMappingProvider提供了如下基本数据类型的转换，此provider是默认设置的，
         * 在Configuration.defaultConfiguration()中返回的DefaultsImpl类，使用的就是JsonSmartMappingProvider，其他：
         * JsonSmartMappingProvider
         * JacksonMappingProvider
         * GsonMappingProvider
         * JsonOrgMappingProvider
         * TapestryMappingProvider
         */
        String json = "{\"date_as_long\" : 1411455611975}"
        String json2 = "{ \"store\": { \"book\": [ { \"category\": \"reference\", \"author\": \"Nigel Rees\", \"title\": \"Sayings of the Century\", \"price\": 8.95 }, { \"category\": \"fiction\", \"author\": \"Evelyn Waugh\", \"title\": \"Sword of Honour\", \"price\": 12.99 }, { \"category\": \"fiction\", \"author\": \"Herman Melville\", \"title\": \"Moby Dick\", \"isbn\": \"0-553-21311-3\", \"price\": 8.99 }, { \"category\": \"fiction\", \"author\": \"J. R. R. Tolkien\", \"title\": \"The Lord of the Rings\", \"isbn\": \"0-395-19395-8\", \"price\": 22.99 } ], \"bicycle\": { \"color\": \"red\", \"price\": 19.95 } }, \"expensive\": 10 }"
        //使用JsonSmartMappingProvider（默认）
        Date date = JsonPath.parse(json).read('$["date_as_long"]', Date.class)
        println date

        //切换Provider成JackMappingProvider
        Configuration configuration = Configuration.builder().mappingProvider(new JacksonMappingProvider()).build()
        Book book = JsonPath.using(configuration).parse(json2).read('$.store.book[0]', Book.class)
        println "现在的MappingProvider：" + configuration.mappingProvider()
        println book
    }
    @Test
    void test04() {
        /**
         * 有时候需要返回当前JsonPath表达式所检索到的全部路径：Option.AS_PATH_LIST
         */
        String json = "{ \"store\": { \"book\": [ { \"category\": \"reference\", \"author\": \"Nigel Rees\", \"title\": \"Sayings of the Century\", \"price\": 8.95 }, { \"category\": \"fiction\", \"author\": \"Evelyn Waugh\", \"title\": \"Sword of Honour\", \"price\": 12.99 }, { \"category\": \"fiction\", \"author\": \"Herman Melville\", \"title\": \"Moby Dick\", \"isbn\": \"0-553-21311-3\", \"price\": 8.99 }, { \"category\": \"fiction\", \"author\": \"J. R. R. Tolkien\", \"title\": \"The Lord of the Rings\", \"isbn\": \"0-395-19395-8\", \"price\": 22.99 } ], \"bicycle\": { \"color\": \"red\", \"price\": 19.95 } }, \"expensive\": 10 }"
        //  Option.AS_PATH_LIST:返回表示评估命中路径的所有子路径字符串列表   net.minidev.json.JSONArray
        Configuration configuration = Configuration.builder().options(Option.AS_PATH_LIST).build()
        List AS_PATH_LIST = JsonPath.using(configuration).parse(json).read('$..author')
        println AS_PATH_LIST
        Assert.assertTrue(AS_PATH_LIST.containsAll(
                "\$['store']['book'][0]['author']",
                "\$['store']['book'][1]['author']",
                "\$['store']['book'][2]['author']",
                "\$['store']['book'][3]['author']"
        ))
        // Option.ALWAYS_RETURN_LIST:使这个实现更符合Goessner规范。所有结果都以列表的形式返回。总是返回list，即便是一个确定的非list类型，也会被包装成list
        Configuration configuration2 = configuration.setOptions(Option.ALWAYS_RETURN_LIST)
        ArrayList ALWAYS_RETURN_LIST = JsonPath.using(configuration2).parse(json).read('$..author')//net.minidev.json.JSONArray
        println ALWAYS_RETURN_LIST
        //当检索不到时返回null对象，否则如果不配置这个，会直接抛出异常
        String ss = "[\n" +
                "          {\n" +
                "             \"foo\" : \"foo1\",\n" +
                "             \"bar\" : \"bar1\"\n" +
                "          },\n" +
                "          {\n" +
                "             \"foo\" : \"foo2\"\n" +
                "          }\n" +
                "     ]"
        Configuration configuration3 = configuration.setOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)
        ArrayList DEFAULT_PATH_LEAF_TO_NULL = JsonPath.using(configuration3).parse(ss).read('$[*].bar')//net.minidev.json.JSONArray
        println DEFAULT_PATH_LEAF_TO_NULL//net.minidev.json.JSONArray
        // 如果设置，则不允许使用通配符，比如$[*].b，会抛出PathNotFoundException异常
        String s = "[\n" +
                "         {\n" +
                "             \"a\" : \"a-val\",\n" +
                "             \"b\" : \"b-val\"\n" +
                "         },\n" +
                "         {\n" +
                "             \"a\" : \"a-val\",\n" +
                "         }\n" +
                "     ]"
        Configuration configuration4 = configuration.setOptions(Option.REQUIRE_PROPERTIES)
        ArrayList REQUIRE_PROPERTIES = JsonPath.using(configuration4).parse(s).read('$[*].b')//net.minidev.json.JSONArray
        println REQUIRE_PROPERTIES//net.minidev.json.JSONArray

        // 在评估路径时抑制所有异常,返回空list
        Configuration configuration5 = configuration.setOptions(Option.SUPPRESS_EXCEPTIONS)
        println configuration5.getOptions()
        ArrayList SUPPRESS_EXCEPTIONS = JsonPath.using(configuration5).parse(s).read('$[*].c')//net.minidev.json.JSONArray
        println SUPPRESS_EXCEPTIONS//net.minidev.json.JSONArray

    }
    @Test
    void returnMap() {
        //
        String jsonMap = "{\"fields\":{\"field1\":1,\"field2\":2,\"field3\":3,\"field4\":\"4\"}}";
        def m = ["name": "value"].toString()
        Json
    }
    @Test
    void odcTest() {
        String response = "{\"errCode\":null,\"errMsg\":null,\"data\":[{\"hidden\":false,\"sid\":\"839\",\"userId\":\"1000106\",\"sessionName\":\"mysql_1479\",\"host\":\"100.69.96.13\",\"port\":\"12003\",\"configUrl\":null,\"cluster\":\"ob1479.root.100.69.96.13\",\"tenant\":\"sys\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_MYSQL\",\"properties\":null,\"gmtCreated\":1609911715000,\"gmtModified\":1609911715000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"837\",\"userId\":\"1000106\",\"sessionName\":\"mysql_2230\",\"host\":\"100.69.96.13\",\"port\":\"12006\",\"configUrl\":null,\"cluster\":\"ob2230.root.100.69.96.13\",\"tenant\":\"mysql\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_MYSQL\",\"properties\":null,\"gmtCreated\":1609911714000,\"gmtModified\":1609911714000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"838\",\"userId\":\"1000106\",\"sessionName\":\"oracle_2230\",\"host\":\"100.69.96.13\",\"port\":\"12006\",\"configUrl\":null,\"cluster\":\"ob2230.root.100.69.96.13\",\"tenant\":\"oracle\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_ORACLE\",\"properties\":null,\"gmtCreated\":1609911714000,\"gmtModified\":1609911714000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"836\",\"userId\":\"1000106\",\"sessionName\":\"oracle_2250\",\"host\":\"100.69.96.13\",\"port\":\"12009\",\"configUrl\":null,\"cluster\":\"ob2250.root.100.69.96.13\",\"tenant\":\"oracle\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_ORACLE\",\"properties\":null,\"gmtCreated\":1609911713000,\"gmtModified\":1609911713000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"834\",\"userId\":\"1000106\",\"sessionName\":\"oracle_2275\",\"host\":\"100.69.100.202\",\"port\":\"22009\",\"configUrl\":null,\"cluster\":\"ob2270.root.100.69.96.13\",\"tenant\":\"oracle\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_ORACLE\",\"properties\":null,\"gmtCreated\":1609911712000,\"gmtModified\":1610019444000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"835\",\"userId\":\"1000106\",\"sessionName\":\"mysql_2250\",\"host\":\"100.69.96.13\",\"port\":\"12009\",\"configUrl\":null,\"cluster\":\"ob2250.root.100.69.96.13\",\"tenant\":\"mysql\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_MYSQL\",\"properties\":null,\"gmtCreated\":1609911712000,\"gmtModified\":1609911712000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"832\",\"userId\":\"1000106\",\"sessionName\":\"oracle_31\",\"host\":\"100.69.96.13\",\"port\":\"10087\",\"configUrl\":null,\"cluster\":null,\"tenant\":\"oracle\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_ORACLE\",\"properties\":null,\"gmtCreated\":1609911711000,\"gmtModified\":1609911711000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"833\",\"userId\":\"1000106\",\"sessionName\":\"mysql_2275\",\"host\":\"100.69.100.202\",\"port\":\"22009\",\"configUrl\":null,\"cluster\":\"ob2270.root.100.69.96.13\",\"tenant\":\"mysql\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_MYSQL\",\"properties\":null,\"gmtCreated\":1609911711000,\"gmtModified\":1609911711000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"831\",\"userId\":\"1000106\",\"sessionName\":\"mysql_31\",\"host\":\"100.69.96.13\",\"port\":\"10087\",\"configUrl\":null,\"cluster\":null,\"tenant\":\"mysql\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_MYSQL\",\"properties\":null,\"gmtCreated\":1609911710000,\"gmtModified\":1609911710000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"}],\"importantMsg\":false}"
        //静态方法
        Object ob = JsonPath.parse(response)//JosnContext对象
        println ob

        println ob.configuration().jsonProvider()
        println ob.getClass().getName()
        println ob.json()//反序列化成LinkedHashMap
        println ob.json().getClass().getName()
        //.dump()
//        ob.set(ob,'$.data[0].fuck', "LEGEND")
//        println ob.read('$.data[0].fuck')
//        println JsonPath.compile('$.data').read('$.userId')
    }
    @Test
    void staticAPI() {
        String response = "{\"errCode\":null,\"errMsg\":null,\"data\":[{\"hidden\":false,\"sid\":\"839\",\"userId\":\"1000106\",\"sessionName\":\"mysql_1479\",\"host\":\"100.69.96.13\",\"port\":\"12003\",\"configUrl\":null,\"cluster\":\"ob1479.root.100.69.96.13\",\"tenant\":\"sys\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_MYSQL\",\"properties\":null,\"gmtCreated\":1609911715000,\"gmtModified\":1609911715000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"837\",\"userId\":\"1000106\",\"sessionName\":\"mysql_2230\",\"host\":\"100.69.96.13\",\"port\":\"12006\",\"configUrl\":null,\"cluster\":\"ob2230.root.100.69.96.13\",\"tenant\":\"mysql\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_MYSQL\",\"properties\":null,\"gmtCreated\":1609911714000,\"gmtModified\":1609911714000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"838\",\"userId\":\"1000106\",\"sessionName\":\"oracle_2230\",\"host\":\"100.69.96.13\",\"port\":\"12006\",\"configUrl\":null,\"cluster\":\"ob2230.root.100.69.96.13\",\"tenant\":\"oracle\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_ORACLE\",\"properties\":null,\"gmtCreated\":1609911714000,\"gmtModified\":1609911714000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"836\",\"userId\":\"1000106\",\"sessionName\":\"oracle_2250\",\"host\":\"100.69.96.13\",\"port\":\"12009\",\"configUrl\":null,\"cluster\":\"ob2250.root.100.69.96.13\",\"tenant\":\"oracle\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_ORACLE\",\"properties\":null,\"gmtCreated\":1609911713000,\"gmtModified\":1609911713000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"834\",\"userId\":\"1000106\",\"sessionName\":\"oracle_2275\",\"host\":\"100.69.100.202\",\"port\":\"22009\",\"configUrl\":null,\"cluster\":\"ob2270.root.100.69.96.13\",\"tenant\":\"oracle\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_ORACLE\",\"properties\":null,\"gmtCreated\":1609911712000,\"gmtModified\":1610019444000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"835\",\"userId\":\"1000106\",\"sessionName\":\"mysql_2250\",\"host\":\"100.69.96.13\",\"port\":\"12009\",\"configUrl\":null,\"cluster\":\"ob2250.root.100.69.96.13\",\"tenant\":\"mysql\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_MYSQL\",\"properties\":null,\"gmtCreated\":1609911712000,\"gmtModified\":1609911712000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"832\",\"userId\":\"1000106\",\"sessionName\":\"oracle_31\",\"host\":\"100.69.96.13\",\"port\":\"10087\",\"configUrl\":null,\"cluster\":null,\"tenant\":\"oracle\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_ORACLE\",\"properties\":null,\"gmtCreated\":1609911711000,\"gmtModified\":1609911711000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"833\",\"userId\":\"1000106\",\"sessionName\":\"mysql_2275\",\"host\":\"100.69.100.202\",\"port\":\"22009\",\"configUrl\":null,\"cluster\":\"ob2270.root.100.69.96.13\",\"tenant\":\"mysql\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_MYSQL\",\"properties\":null,\"gmtCreated\":1609911711000,\"gmtModified\":1609911711000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"},{\"hidden\":false,\"sid\":\"831\",\"userId\":\"1000106\",\"sessionName\":\"mysql_31\",\"host\":\"100.69.96.13\",\"port\":\"10087\",\"configUrl\":null,\"cluster\":null,\"tenant\":\"mysql\",\"dbUser\":\"legend\",\"password\":\"123456\",\"defaultDBName\":\"legend\",\"dbMode\":\"OB_MYSQL\",\"properties\":null,\"gmtCreated\":1609911710000,\"gmtModified\":1609911710000,\"proxyroPassword\":null,\"sysUser\":null,\"sysUserPassword\":null,\"sessionTimeoutS\":10,\"connectType\":\"CONNECT_TYPE_OB\",\"extendInfo\":\"{}\"}],\"importantMsg\":false}"
        //静态方法
        Object ob = JsonPath.parse(response)//JosnContext对象
        println ob

        println ob.configuration().jsonProvider()
        println ob.getClass().getName()
        println ob.json()//反序列化成LinkedHashMap
        println ob.json().getClass().getName()
    }
    @Test
    void toJavaBean(){
        String jsonString = "{\"name\":\"Mahesh\", \"age\":21}"
        Object person = JsonPath.parse(jsonString)//JosnContext对象
        println person.json().getClass().getName()//转为java的LinkedHashMap对象
    }
}
