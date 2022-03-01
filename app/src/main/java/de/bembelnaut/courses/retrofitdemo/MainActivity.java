package de.bembelnaut.courses.retrofitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.text_view_result);

        // serialize nulls in patch request
        Gson gson = new GsonBuilder().serializeNulls().create();

        // add logging
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request newRequest = originalRequest.newBuilder()
                                .header("Interceptor-Header", "Bar")
                                .build();

                        return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(httpLoggingInterceptor)
                .build();

        // end base url must end with a slash; it must relative
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        //getPosts();
        //getComments();
        //createPost();
        updatePost();

        //deletePost();
    }

    private void deletePost() {
        Call<Void> call = jsonPlaceHolderApi.deletePost(1);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                textViewResult.setText("Code: " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });

    }

    private void updatePost() {
        //Post post = new Post(1, null, "Lorem ipsumn");
        //Call<Post> call = jsonPlaceHolderApi.patchPost(1, post);

        //Post post = new Post(1, null, "Lorem ipsumn");
        //Call<Post> call = jsonPlaceHolderApi.patchPost(1, post);

        //Post post = new Post(1, null, "Lorem ipsumn");
        //Call<Post> call = jsonPlaceHolderApi.putPostWithHeader("Hey hoo", 1, post);

        Post post = new Post(1, null, "Lorem ipsumn");
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Dynamic-Header-Map", "Foo");
        Call<Post> call = jsonPlaceHolderApi.patchPostWithHeaders(headerMap,1, post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                Post p = response.body();

                String content = "";
                content += "Message-Code: " + response.code() + "\n";
                content += "ID: " + p.getId() + "\n";
                content += "User-ID: " + p.getUserId() + "\n";
                content += "Title: " + p.getTitle() + "\n";
                content += "Text: " + p.getText() + "\n\n";

                textViewResult.append(content);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void createPost() {
        Post post = new Post(23, "New title", "Hello World!");
        Call<Post> call = jsonPlaceHolderApi.createPost(post);

        //Call<Post> call = jsonPlaceHolderApi.createPost(23, "New Title", "Hi There");

        /*Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("userId", "25");
        fieldMap.put("title", "Möööp");
        Call<Post> call = jsonPlaceHolderApi.createPost(fieldMap);*/

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText(response.code());
                    return;
                }

                Post p = response.body();

                String content = "";
                content += "ID: " + p.getId() + "\n";
                content += "User-ID: " + p.getUserId() + "\n";
                content += "Title: " + p.getTitle() + "\n";
                content += "Text: " + p.getText() + "\n\n";

                textViewResult.append(content);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });

    }

    private void getComments() {
        //Call<List<Comment>> call = jsonPlaceHolderApi.getComments(3);

        //Call<List<Comment>> call = jsonPlaceHolderApi.getComments("posts/3/comments");

        // overwrite base url
        Call<List<Comment>> call = jsonPlaceHolderApi.getComments("https://jsonplaceholder.typicode.com/posts/3/comments");

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText(response.code());
                    return;
                }

                List<Comment> posts = response.body();
                posts.forEach(p -> {
                    String content = "";
                    content += "ID: " + p.getId() + "\n";
                    content += "Post-ID: " + p.getPostId() + "\n";
                    content += "Name: " + p.getName() + "\n";
                    content += "Email: " + p.getEmail() + "\n";
                    content += "Text: " + p.getText() + "\n\n";

                    textViewResult.append(content);
                });
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void getPosts() {
        //Call<List<Post>> call = jsonPlaceHolderApi.getPosts( new Integer[] {1, 4, 6} ,null, null);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userId", "1");
        parameters.put("_sort", "id");
        parameters.put("_order", "desc");
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts(parameters);

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText(response.code());
                    return;
                }

                List<Post> posts = response.body();
                posts.forEach(p -> {
                    String content = "";
                    content += "ID: " + p.getId() + "\n";
                    content += "User-ID: " + p.getUserId() + "\n";
                    content += "Title: " + p.getTitle() + "\n";
                    content += "Text: " + p.getText() + "\n\n";

                    textViewResult.append(content);
                });
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }
}