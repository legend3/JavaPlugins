import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 官网:http://jackson.codehaus.org/
 * tutorial:http://jackson.codehaus.org/
 *
 * jackson库于2012.10.8号公布了最新的2.1版。因为有不少变更，这里做一个记录。
 *
 * jackson源码今朝托管于github，地址：https://github.com/fasterxml/
 *
 * 一、jackson 2.x
 * jackson 2.x版供给了三个jar包供：
 *
 * 1．core库：streaming parser/generator，即流式的解析器和生成器。
 * http://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.1.0/jackson-core-2.1.0.jar
 * 2．annotations库：databinding annotations，即带注释的数据绑定包。
 * http://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.1.0/jackson-annotations-2.1.0.jar
 * 3．databind库：objectmapper， json tree model，即对象映射器，json树范型。
 * http://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.1.0/jackson-databind-2.1.0.jar
 *
 * 从jackson 2.0起，核心组件包含：jackson-annotations、jackson-core、jackson-databind。数据格局模块包含：smile、csv、xml、yam http://mvnrepository.com/artifact/com.fasterxml.jackson.core
 *
 * 二、jackson 1.9.10
 * ackson 1.9.10版的库有多种选择：
 *
 * 单个库：供给了core-asl、mapper-asl、core-lpgl、mapper-lgpl、jax-rs、jax-xc、mrbean、smile等jar包。
 * jackson all库：包含了上方所有的jar包，打包成了单个jar文件。
 * http://jackson.codehaus.org/1.9.10/jackson-all-1.9.10.jar
 * jackson mini库：包含了jackson-core库，打消了注释库、容许证文件、用于应用受限的景象，比如移动设备，jar包的尺寸明显削减。
 * http://jackson.codehaus.org/1.9.10/jackson-mini-1.9.10.jar
 * smile tool对象：一个号令行对象，用于在smile格局和json格局之间彼此转换。
 * http://jackson.codehaus.org/1.9.10/smile-tool-1.9.10.jar
 * 注：smile是二进制的json数据格局，等同于标准的json数据格局。smile格局于2010年公布，于2010年9月jackson 1.6版开端支撑。
 * 支撑smile格局的框架：
 * （1） jackson json processor：完全支撑smile格局，包含流式接见，数据绑定和树范型。
 * （2） libsmile：一个c说话库，支撑读写smile数据。
 * （3） elastic search：支撑把smile格局作为输入/输出的源。
 * （4） protostuff：此项目支撑smile格局作为底层数据格局，也用于rpc的格局之一。
 *
 * 作者：一天一夜00
 * 链接：https://www.jianshu.com/p/9ce3d110017e
 * 来源：简书
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
public class JacksonTester {
    @Test
    public void test01() {
        /**
         * 创建一个包含学生详细信息的JSON字符串，
         * 并将其反序列化为student对象，
         * 然后将其序列化为JSON字符串。
         */
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{\"name\":\"Mahesh\", \"age\":21}";
        //map json to student
        try{
            Student student = mapper.readValue(jsonString, Student.class);
            System.out.println(student);
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(student);
            System.out.println(jsonString);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace(); }
    }
    @Test
    public void test02(){
        /**
         * ObjectMapper是Jackson库的主要actor类。
         * ObjectMapper类ObjectMapper提供了从基本POJO（普通旧Java对象）或从通用JSON树模型（JsonNode）读取和写入JSON的功能，
         * 以及执行转换的相关功能。
         * 它还可以高度自定义，可以使用不同样式的JSON内容，并支持更高级的Object概念，如多态和对象标识。
         * ObjectMapper还充当更高级的ObjectReader和ObjectWriter类的工厂
         */
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{\"name\":\"Mahesh\", \"age\":21}";
        //map json to student
        try{
            Student student = mapper.readValue(jsonString, Student.class);//反序列化(为Student对象)
            System.out.println(student);
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(student);//序列化
            System.out.println(jsonString);

            ArrayList array = JsonPath.parse(jsonString).read("$.*");//反序列化为数组对象
            System.out.println(array);
        }
        catch (JsonParseException e) { e.printStackTrace();}
        catch (JsonMappingException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
    }
    @Test
    public void test03() {
        /**
         * 创建了Student类。 我们将创建一个student.json文件，该文件将具有Student对象的json表示
         */
        JacksonTester tester = new JacksonTester();
        try {
            Student student = new Student();
            student.setAge(10);
            student.setName("Mahesh");
            tester.writeJSON(student);
            Student student1 = tester.readJSON();//自动创建一个有Student对象的json表示的文件,studnet.json
            System.out.println(student1);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test04() {
        /**
         * Data Binding API用于使用属性访问器或使用注释将JSON转换为POJO（Plain Old Java Object）和从POJO（Plain Old Java Object）转换JSON。
         * 它有两种类型。
         *
         * Simple Data Binding - 将JSON转换为Java的Map，Arraylist，String，数字，Boolean和null。
         *
         * Full Data Binding - 将JSON转换为任何JAVA类型。
         *
         * ObjectMapper为两种类型的数据绑定读取/写入JSON。 数据绑定是最方便的方式，类似于XML的JAXB parer。
         */
        JacksonTester tester = new JacksonTester();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> studentDataMap = new HashMap<String,Object>();
            int[] marks = {1,2,3};
            Student student = new Student();
            student.setAge(10);
            student.setName("Mahesh");
            // JAVA Object
            studentDataMap.put("student", student);
            // JAVA String
            studentDataMap.put("name", "Mahesh Kumar");
            // JAVA Boolean
            studentDataMap.put("verified", Boolean.FALSE);
            // Array
            studentDataMap.put("marks", marks);
            mapper.writeValue(new File("student.json"), studentDataMap);
            //result student.json
            //{
            //   "student":{"name":"Mahesh","age":10},
            //   "marks":[1,2,3],
            //   "verified":false,
            //   "name":"Mahesh Kumar"
            //}
            studentDataMap = mapper.readValue(new File("student.json"), Map.class);
            System.out.println(studentDataMap.get("student"));
            System.out.println(studentDataMap.get("name"));
            System.out.println(studentDataMap.get("verified"));
            System.out.println(studentDataMap.get("marks"));
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test05() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = "{\"name\":\"Mahesh Kumar\",  \"age\":21,\"verified\":false,\"marks\": [100,90,85]}";
            JsonNode rootNode = mapper.readTree(jsonString);
            JsonNode nameNode = rootNode.path("name");
            System.out.println("Name: "+ nameNode.textValue());
            JsonNode ageNode = rootNode.path("age");
            System.out.println("Age: " + ageNode.intValue());
            JsonNode verifiedNode = rootNode.path("verified");
            System.out.println("Verified: " + (verifiedNode.booleanValue() ? "Yes":"No"));
            JsonNode marksNode = rootNode.path("marks");
            Iterator<JsonNode> iterator = marksNode.elements();
            System.out.print("Marks: [ ");
            while (iterator.hasNext()) {
                JsonNode marks = iterator.next();
                System.out.print(marks.intValue() + " ");
            }
            System.out.println("]");
        }
        catch (JsonParseException e) { e.printStackTrace(); }
        catch (JsonMappingException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace();
        }
    }
    @Test
    public void test06() {
        try {
            JsonFactory jsonFactory = new JsonFactory();
            JsonGenerator jsonGenerator = jsonFactory.createGenerator(new File("student.json"), JsonEncoding.UTF8);
            jsonGenerator.writeStartObject();
            // "name" : "Mahesh Kumar"
            jsonGenerator.writeStringField("name", "Mahesh Kumar");
            // "age" : 21
            jsonGenerator.writeNumberField("age", 21);
            // "verified" : false
            jsonGenerator.writeBooleanField("verified", false);
            // "marks" : [100, 90, 85]
            jsonGenerator.writeFieldName("marks");
            // [
            jsonGenerator.writeStartArray();
            // 100, 90, 85
            jsonGenerator.writeNumber(100);
            jsonGenerator.writeNumber(90);
            jsonGenerator.writeNumber(85);
            // ]
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
            jsonGenerator.close();
            //result student.json
            //{
            //   "name":"Mahesh Kumar",
            //   "age":21,
            //   "verified":false,
            //   "marks":[100,90,85]
            //}
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> dataMap = mapper.readValue(new File("student.json"), Map.class);
            System.out.println(dataMap.get("name"));
            System.out.println(dataMap.get("age"));
            System.out.println(dataMap.get("verified"));
            System.out.println(dataMap.get("marks"));
        }
        catch (JsonParseException e) { e.printStackTrace(); }
        catch (JsonMappingException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
    }
    @Test
    public void test066() {
        JacksonTester tester = new JacksonTester();
        try {
            JsonFactory jsonFactory = new JsonFactory();
            JsonGenerator jsonGenerator = jsonFactory.createGenerator(new File(
                    "student.json"), JsonEncoding.UTF8);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("name", "Mahesh Kumar");
            jsonGenerator.writeNumberField("age", 21);
            jsonGenerator.writeBooleanField("verified", false);
            jsonGenerator.writeFieldName("marks");
            jsonGenerator.writeStartArray(); // [
            jsonGenerator.writeNumber(100);
            jsonGenerator.writeNumber(90);
            jsonGenerator.writeNumber(85);
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
            jsonGenerator.close();
            //result student.json
            //{
            //   "name":"Mahesh Kumar",
            //   "age":21,
            //   "verified":false,
            //   "marks":[100,90,85]
            //}
            JsonParser jsonParser = jsonFactory.createParser(new File("student.json"));
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                //get the current token
                String fieldname = jsonParser.getCurrentName();
                if ("name".equals(fieldname)) {
                    //move to next token
                    jsonParser.nextToken();
                    System.out.println(jsonParser.getText());
                }
                if("age".equals(fieldname)){
                    //move to next token
                    jsonParser.nextToken();
                    System.out.println(jsonParser.getNumberValue());
                }
                if("verified".equals(fieldname)){
                    //move to next token
                    jsonParser.nextToken();
                    System.out.println(jsonParser.getBooleanValue());
                }
                if("marks".equals(fieldname)){
                    //move to [
                    jsonParser.nextToken();
                    // loop till token equal to "]"
                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        System.out.println(jsonParser.getNumberValue());
                    }
                }
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test07() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{\"name\":\"Mahesh\", \"age\":21}";
        //map json to student
        try{
            Student student = mapper.readValue(jsonString, Student.class);
            System.out.println(student);
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(student);
            System.out.println(jsonString);
        }
        catch (JsonParseException e) { e.printStackTrace();}
        catch (JsonMappingException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace();
        }
    }
    //辅助调用方法
    private void writeJSON(Student student) throws JsonGenerationException, JsonMappingException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("student.json"), student);
    }
    private Student readJSON() throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        Student student = mapper.readValue(new File("student.json"), Student.class);
        return student;
    }
}

