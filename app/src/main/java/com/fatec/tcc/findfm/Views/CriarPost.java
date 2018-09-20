package com.fatec.tcc.findfm.Views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.fatec.tcc.findfm.Infrastructure.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.SharedRequestQueue;
import com.fatec.tcc.findfm.Infrastructure.Request.VolleyMultipartRequest;
import com.fatec.tcc.findfm.Model.Business.Post;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Request.PostRequest;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpMethod;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.ImagemUtils;
import com.fatec.tcc.findfm.databinding.ActivityCriarPostBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CriarPost extends AppCompatActivity {

    private ActivityCriarPostBinding binding;
    private static final int PICK_IMAGE = 1;
    private ImageView fotoPublicacao;
    private ProgressDialog dialog;
    private Bundle param = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_criar_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.param = getIntent().getBundleExtra("com.fatec.tcc.findfm.Views.Adapters.AdapterMeusAnuncios");

        if( this.param != null ) {
            if( !this.param.isEmpty() ) {

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss", Locale.ENGLISH);
                Date date = new Date();

                String dateInString = param.getString("data", "");

                try {
                    date = formatter.parse(dateInString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Post post = new Post()
                        .setTitulo(this.param.getString("titulo", ""))
                        .setDescricao(this.param.getString("descricao", ""))
                        .setAutor(new Usuario().setNomeCompleto(param.getString("autor", "")))
                        .setData(date);

                binding.incluirContent.setPost(post);
            }
        } else {
            binding.incluirContent.setPost(new Post());
        }

        binding.executePendingBindings();

        fotoPublicacao = findViewById(R.id.fotoPublicacao);
        fotoPublicacao.setVisibility(View.GONE);

        //Somente contratante pode colocar titulo para o anuncio
        EditText txtTitulo = findViewById(R.id.txtTitulo);
        if(FindFM.getTipoUsuario(this) != TiposUsuario.CONTRATANTE){
            txtTitulo.setVisibility(View.GONE);
        }

        FloatingActionButton fab = findViewById(R.id.fab_foto);
        fab.setOnClickListener(view -> startActivityForResult(Intent.createChooser(ImagemUtils.pickImageIntent(), "Escolha uma foto"), PICK_IMAGE));

        FloatingActionButton video = findViewById(R.id.fab_video);
        video.setOnClickListener(view -> Snackbar.make(view, "Selecionar video", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());


        dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

    }

    public void btnRegistrar_Click(View v) {
        EditText txtTitulo = findViewById(R.id.txtTitulo);
        txtTitulo.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_salvar:
                //TODO: salvar post
                initRequest(binding.incluirContent.getPost());
                Toast.makeText(this, "Salvando post...", Toast.LENGTH_SHORT).show();
                //uploadFiles();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                this.fotoPublicacao.setImageBitmap(BitmapFactory.decodeStream(getApplicationContext()
                        .getContentResolver().openInputStream(Objects.requireNonNull(data.getData()))));
                this.fotoPublicacao.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadFiles() {
        // loading or check internet connection or something...
        // ... then upload
        String url = HttpUtils.buildUrl(getResources(),"upload");
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(HttpMethod.PUT.getCodigo(), url, response -> {
            String resultResponse = new String(response.data);
            try {
                JSONObject result = new JSONObject(resultResponse);
                String status = result.getString("status");
                String message = result.getString("message");

                if (status.equals(ResponseCode.GenericSuccess)) {
                    // tell everybody you have succed upload image and post strings
                    Log.i("Messsage", message);
                } else {
                    Log.i("Unexpected", message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            NetworkResponse networkResponse = error.networkResponse;
            String errorMessage = "Unknown error";
            if (networkResponse == null) {
                if (error.getClass().equals(TimeoutError.class)) {
                    errorMessage = "Request timeout";
                } else if (error.getClass().equals(NoConnectionError.class)) {
                    errorMessage = "Failed to connect server";
                }
            } else {
                String result = new String(networkResponse.data);
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getString("status");
                    String message = response.getString("message");

                    Log.e("Error Status", status);
                    Log.e("Error Message", message);

                    if (networkResponse.statusCode == 404) {
                        errorMessage = "Resource not found";
                    } else if (networkResponse.statusCode == 401) {
                        errorMessage = message+" Please login again";
                    } else if (networkResponse.statusCode == 400) {
                        errorMessage = message+ " Check your inputs";
                    } else if (networkResponse.statusCode == 500) {
                        errorMessage = message+" Something is getting wrong";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i("Error", errorMessage);
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("cover", new DataPart("file_cover.jpg", ImagemUtils.getFileDataFromDrawable(getBaseContext(), fotoPublicacao.getDrawable()), "image/jpeg"));

                return params;
            }
        };

        SharedRequestQueue.addToRequestQueue(this, multipartRequest);
    }


    private void initRequest(Post post){
        HttpTypedRequest<PostRequest, ResponseBody, ErrorResponse> postRequest = new HttpTypedRequest<>(
                this,
                HttpMethod.POST.getCodigo(),
                PostRequest.class,
                ResponseBody.class,
                ErrorResponse.class,
                (ResponseBody response) -> {
                    this.dialog.hide();
                    if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {

                    }
                },
                (ErrorResponse error) -> {
                    dialog.hide();
                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Ops!", R.drawable.ic_error,
                            error.getMessage(),"OK",
                            (dialog, id) -> { }).create().show();
                },
                (Exception error) -> {
                    dialog.hide();
                    error.printStackTrace();
                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Ops!", R.drawable.ic_error,
                            "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                    "\nVerifique sua conexão com a Internet e tente novamente","OK",
                            (dialog, id) -> { }).create().show();
                }
        );

        PostRequest param = new PostRequest();
        param.setTitulo(post.getTitulo())
             .setDescricao(post.getDescricao());

        postRequest.setFullUrl(HttpUtils.buildUrl(getResources(),"post/create"));
        postRequest.setRequestObject(param);
        dialog.show();
        postRequest.execute(this);

    }

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    public void setPost(Post param){
        binding.incluirContent.setPost(param);
    }
}
