//@Data
//@AllArgsConstructor
//@NoArgsConstructor
public class Book {
    private String category;
    private String author;
    private String title;
    private String price;

    public Book() {
    }

    public Book(String category, String author, String title, String price) {
        this.category = category;
        this.author = author;
        this.title = title;
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Book{" +
                "category='" + category + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
