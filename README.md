# Recordo
`Recordo` is a declarative testing JUnit 5 extension for fast, deterministic, and accurate tests.

# Usage
### Add maven dependency
```xml
<dependency>
    <groupId>com.cariochi</groupId>
    <artifactId>recordo</artifactId>
    <version>1.1.6</version>
    <scope>test</scope>
</dependency>
```

### Add annotation

```java
@ExtendWith(RecordoExtension.class)
class BookServiceTest {
    ...
}
```

### Enable Json Converter to be used in Recordo (Optional)  

#### Jackson Mapper

```java
    @EnableRecordo
    private ObjectMapper objectMapper;
```

#### Gson

```java
    @EnableRecordo
    private Gson gson;
```

# Data preparation

Load objects from json files. 

Annotations: `@Given`.

- If the file is absent, a new random data file will be created.

### Example

```java
    @Test
    void should_create_book(
        @Given("/books/new_book.json") Book book
    ) {
        Book created = bookService.create(book);
        // assertions
    }
```

# Assertions 

Assert that actual value equals to expected.

Annotations: `@Verify`. 

- If a file is absent, the actual result will be saved as expected.
- If an assertion fails new "actual" object file will be created.

### Example

```java
    @Test
    void should_get_book_by_id(
            @Verify("/books/book.json") Expected<Book> expected
    ) {
        Book actual = bookService.findById(1L);
        expected.assertEquals(actual);
    }
```

# Mocking HTTP resources

Record and replay HTTP network interaction for a test.

Annotations: `@MockHttp`.

### Initialization

#### OkHttp

```java
    @EnableRecordo
    private OkHttpClient client;
```

#### Apache HttpClient

```java
    @EnableRecordo
    private HttpClient httpClient;
```

### Example

```java
    @Test
    @MockHttp("/mockhttp/should_retrieve_gists.rest.json")
    void should_retrieve_gists() {
        ...
        final List<GistResponse> gists = gitHubClient.getGists();
        ...
    }
```

# Declarative MockMvc

Use Spring MockMvc in declarative way.

Annotations: `@GET`, `@POST`, `@PUT`, `@PATCH`, `@DELETE`, `@Headers`, `@Body`.

### Initialization
```java
    @EnableRecordo
    private MockMvc mockMvc;
```

### Examples

```java
    @Test
    void should_get_books(
            @GET("/users/{id}/books?sort={sort}") @Headers("locale: UA") Request<Page<Book>> request
    ) {
        ...
        Response<Page<Book>> response = request.execute(1, "name");
        Page<Book> books = response.getContent();
        // assertions
    }

    @Test
    void should_get_books(
           @GET("/users/1/books?sort=name") @Headers("locale: UA") Response<Page<Book>> response
    ) {
        Page<Book> books = response.getContent();
        // assertions
    }

    @Test
    void should_get_books(
           @GET("/users/1/books?sort=name") @Headers("locale: UA") Page<Book> books
    ) {
        // assertions
    }

    @Test
    void should_save_book(
            @POST("/books") Request<Book> request
    ) {
        ...
        Response<Book> response = request.withBody(new Book()).execute();
        Book book = response.getContent();
        // assertions
    }

    @Test
    void should_save_book(
            @POST("/books") @Body("/mockmvc/new_book.json") Request<Book> request
    ) {
        Response<Book> response = request.execute();
        Book book = response.getContent();
        // assertions
    }

    @Test
    void should_save_book(
            @POST("/books") @Body("/mockmvc/new_book.json") Response<Book> response
    ) {
        Book book = response.getContent();
        // assertions
    }

    @Test
    void should_save_book(
            @POST("/books") @Body("/mockmvc/new_book.json") Book book
    ) {
        // assertions
    }

    @Test
    void should_update_book(
            @PUT("/books") @Body("/mockmvc/changed_book.json") Book book
    ) {
         // assertions
    }

    @Test
    void should_patch_book(
            @PATCH("/books/1") @Body("/mockmvc/book.json") Book book
    ) {
        // assertions
    }

    @Test
    void should_delete_book(
            @DELETE("/users/1") Request<Void> request
    ) {
        ...
        Response<Void> response = request.execute();
        // assertions
    }

    @Test
    void should_delete_book(
            @DELETE("/users/1") Response<Void> response
    ) {
        // assertions
    }

```
