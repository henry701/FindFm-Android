package com.fatec.tcc.findfm.Views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.fatec.tcc.findfm.Infrastructure.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Infrastructure.Request.UploadResourceService;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class CriarPost extends AppCompatActivity implements Observer{

    private ActivityCriarPostBinding binding;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private ImageView fotoPublicacao;
    private VideoView videoView;
    private ProgressDialog dialog;
    private Bundle param = new Bundle();

    private boolean fotoUpload;
    private String fotoBytesId;
    private byte[] fotoBytes;
    private String fotoBytes_ContentType;

    private boolean videoUpload;
    private String videoBytesId;
    private byte[] videoBytes;
    private String videoBytes_ContentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_criar_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.param = getIntent().getBundleExtra("com.fatec.tcc.findfm.Views.Adapters.AdapterMeusAnuncios");
        ImageView imageView = findViewById(R.id.circularImageView);
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
            ImagemUtils.setImagemToImageView(imageView, this);
            binding.incluirContent.setPost(new Post().setAutor(new Usuario().setNomeCompleto(FindFM.getNomeUsuario(this))));
        }

        binding.executePendingBindings();

        fotoPublicacao = findViewById(R.id.fotoPublicacao);
        fotoPublicacao.setVisibility(View.GONE);

        videoView = findViewById(R.id.videoView);
        videoView.setVisibility(View.GONE);

        //Somente contratante pode colocar titulo para o anuncio
        EditText txtTitulo = findViewById(R.id.txtTitulo);
        if(FindFM.getTipoUsuario(this) != TiposUsuario.CONTRATANTE){
            txtTitulo.setVisibility(View.GONE);
        }

        FloatingActionButton fab = findViewById(R.id.fab_foto);
        fab.setOnClickListener(view -> startActivityForResult(Intent.createChooser(ImagemUtils.pickImageIntent(), "Escolha uma foto"), PICK_IMAGE));

        FloatingActionButton video = findViewById(R.id.fab_video);
        video.setOnClickListener(view -> startActivityForResult(Intent.createChooser(ImagemUtils.pickVideoIntent(), "Escolha o video"), PICK_VIDEO));

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
                UploadResourceService resourceService = new UploadResourceService(this);
                resourceService.addObserver(this);
                this.dialog.show();
                Toast.makeText(this, "Salvando post...", Toast.LENGTH_SHORT).show();
                if(fotoBytes != null) {
                    fotoUpload = true;
                    resourceService.uploadFiles(fotoBytes, fotoBytes_ContentType, true);
                }
                if(videoBytes != null) {
                    videoUpload = true;
                    resourceService.uploadFiles(videoBytes, videoBytes_ContentType, false);
                }

                if(!fotoUpload && !videoUpload){
                    initRequest(binding.incluirContent.getPost());
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getApplicationContext()
                        .getContentResolver().openInputStream(Objects.requireNonNull(data.getData())));
                this.fotoPublicacao.setImageBitmap(bitmap);
                this.fotoPublicacao.setVisibility(View.VISIBLE);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                this.fotoBytes = stream.toByteArray();
                this.fotoBytes_ContentType = "image/jpeg";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_VIDEO && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Uri u = data.getData();
                MediaController m = new MediaController(this);

                videoView.setMediaController(m);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(u);
                videoView.setVisibility(View.VISIBLE);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream fis = getContentResolver().openInputStream(u);

                byte[] buf = new byte[1024];
                int n;
                while (-1 != (n = fis.read(buf)))
                    baos.write(buf, 0, n);

                this.videoBytes = baos.toByteArray();
                //TODO: arrumar
                this.videoBytes_ContentType = "video/mpeg";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                        AlertDialogUtils.newSimpleDialog__OneButton(this,
                                "Sucesso!", R.drawable.ic_error,
                                "Post cadastrado com sucesso","OK",
                                (dialog, id) -> {
                                                dialog.dismiss();
                                                super.onBackPressed(); }).create().show();

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
             .setDescricao(post.getDescricao())
             .setImagemId(post.getIdFoto())
             .setVideoId(post.getIdVideo());

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

    @Override
    public void update(Observable upload, Object arg) {
        //TODO: fazer tentar novamente
        if(upload instanceof UploadResourceService){
            runOnUiThread(() -> {
                if(arg instanceof ErrorResponse){
                    ErrorResponse error = (ErrorResponse) arg;
                    dialog.hide();

                    String result = (String) arg;
                    if(result.equals("foto"))
                        fotoUpload = false;
                    else
                        videoUpload = false;

                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Ops!", R.drawable.ic_error,
                            error.getMessage(),"OK",
                            (dialog, id) -> { }).create().show();
                } else if (arg instanceof Exception){
                    Exception error = (Exception) arg;
                    dialog.hide();

                    String result = (String) arg;
                    if(result.equals("foto"))
                        fotoUpload = false;
                    else
                        videoUpload = false;

                    error.printStackTrace();
                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Ops!", R.drawable.ic_error,
                            "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                    "\nVerifique sua conexão com a Internet e tente novamente","OK",
                            (dialog, id) -> { }).create().show();
                }
                else if (arg instanceof String) {
                    String result = (String) arg;
                    String[] resultados = result.split(",");

                    //Por algum motivo as vezes não retorna o id, isso é só pra nao dar exception

                    if(resultados.length == 1){
                        dialog.hide();
                        AlertDialogUtils.newSimpleDialog__OneButton(this,
                                "Ops!", R.drawable.ic_error,
                                "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                        "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                (dialog, id) -> { }).create().show();
                        return;
                    }

                    if(resultados[0].equals("foto")){
                        fotoUpload = false;
                        fotoBytesId = resultados[1];
                        binding.incluirContent.getPost().setIdFoto(fotoBytesId);
                    }
                    else if(resultados[0].equals("video")){
                        videoUpload = false;
                        videoBytesId = resultados[1];
                        binding.incluirContent.getPost().setIdVideo(videoBytesId);
                    }

                    if(!videoUpload && !fotoUpload){
                        initRequest(binding.incluirContent.getPost());
                    }
                }
            });
        }
    }
}
