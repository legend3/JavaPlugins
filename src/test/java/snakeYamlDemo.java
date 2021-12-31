import org.testng.Assert;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.List;
import java.util.Map;

public class snakeYamlDemo {
    /**
     *
     * 1，使用Yaml类，创建一个Yaml对象，所有的解析操作都是从这个对象开始；
     * 2，声明了一个yaml的字符串（当然也可以使用yaml文档等），定义了一个对象：key: hello yaml；
     * 3，使用Yaml对象的load方法 public Object load(String yaml)加载一段yaml字符串，返回解析之后的对象；
     *
     * load/loadAll/loadAs 方法使用
     * Yaml的load方法可以传入多种参数类型：
     * public Object load(String yaml)
     * public Object load(InputStream io)
     * public Object load(Reader io)
     *
     *
     */
    @Test
    public void testLoad() {
        String str = "key: hello yaml";
        Yaml yaml = new Yaml();
        Object ret = yaml.load(str);//字符串(yaml文件yaml字符串)转解析后的对象(Map)
        System.out.println(ret.getClass().getName());
    }
    @Test
    public void testType() {
        Yaml yaml = new Yaml();
        //应该由相应的srpingboot容器管理
        List<String> list = yaml.load(this.getClass().getClassLoader().getResourceAsStream("test.yml"));
        System.out.println(list);
    }
    @Test
    public void testMap() {
        /**
         * yaml内容为一个对象(或map)
         */
        Yaml yaml = new Yaml();
        Map<String, Object> ret = yaml.load(this.getClass().getClassLoader().getResourceAsStream("test2.yml"));
        System.out.println(ret);//{sample1={r=10}, sample2={other=haha}, sample3={x=100, y=100}}
    }
    @Test
    public void testAll() {
        /**
         * 三个yaml配置片段
         */
        Yaml yaml = new Yaml();
        Iterable<Object> ret = yaml.loadAll(this.getClass().getClassLoader().getResourceAsStream("test3.yml"));
        for(Object o : ret) {
            System.out.println(o);
        }
    }
    @Test
    public void testObject() {
        /**
         * 转换对象
         */
        Yaml yaml = new Yaml();
        Address ret = yaml.loadAs(this.getClass().getClassLoader().getResourceAsStream("address.yml"), Address.class);
        System.out.println(ret);//ret就是一个Address的实例
        Assert.assertNotNull(ret);//判断是否为空
        Assert.assertEquals("MI",ret.getState());
    }
    @Test
    public void testObject2() throws Exception {
        /**
         * 注意第一行，我们使用---代表一个yaml文档的开始，并且立刻使用!!告诉下面的类型为Person。
         * 这样配置之后，我们就可以直接使用load方法来加载对象了
         */
        Yaml yaml = new Yaml();
        Person ret = (Person) yaml.load(this.getClass().getClassLoader().getResourceAsStream("person2.yml"));
        Assert.assertNotNull(ret);
        Assert.assertEquals("Mike", ret.getName());
    }
    @Test
    public void testObject3() {
        /**
         * 在创建Yaml对象的时候，传入了一个new Constructor(Person.class)对象，即指定了，yaml文件的root对象使用Person类型。
         * 注意这个Constructor是org.yaml.snakeyaml.constructor.Constructor对象。
         */
        Yaml yaml = new Yaml(new Constructor(Person.class));
        Person ret = yaml.load(this.getClass().getClassLoader().getResourceAsStream("person3.yml"));
        Assert.assertNotNull(ret);
        Assert.assertEquals("Mike", ret.getName());
    }

}
