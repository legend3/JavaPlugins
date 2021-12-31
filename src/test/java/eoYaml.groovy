import com.amihaiemil.eoyaml.Yaml
import com.amihaiemil.eoyaml.YamlMapping
import com.amihaiemil.eoyaml.YamlPrinter
import org.testng.annotations.Test

class eoYaml {

 @Test
    void test6() {
     Map<String, Integer> grades = new HashMap<>();
     grades.put("Math", 9);
     grades.put("CS", 10);
     YamlMapping student = Yaml.createYamlDump(new Student ("John", "Doe", 20, grades)).dumpMapping();
     YamlPrinter printer = Yaml.createYamlPrinter(new FileWriter("student.yml"))
     printer.print(student)
     }
}
