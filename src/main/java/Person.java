
import java.time.LocalTime;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
public class Person {
    private String name;
    private String age;
    private LocalTime DateTime;

    public Person() {
    }

    public Person(String name, String age, LocalTime dateTime) {
        this.name = name;
        this.age = age;
        DateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public LocalTime getDateTime() {
        return DateTime;
    }

    public void setDateTime(LocalTime dateTime) {
        DateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", DateTime=" + DateTime +
                '}';
    }
}
