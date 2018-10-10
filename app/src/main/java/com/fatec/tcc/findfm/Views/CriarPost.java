package com.fatec.tcc.findfm.Views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Controller.Midia.FullScreenMediaController;
import com.fatec.tcc.findfm.Infrastructure.Request.DownloadResourceService;
import com.fatec.tcc.findfm.Infrastructure.Request.UploadResourceService;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Post;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Request.ComentarRequest;
import com.fatec.tcc.findfm.Model.Http.Request.PostRequest;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpMethod;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.MidiaUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Adapters.AdapterComentario;
import com.fatec.tcc.findfm.databinding.ActivityCriarPostBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class CriarPost extends AppCompatActivity implements Observer{

    private ActivityCriarPostBinding binding;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int PICK_AUDIO = 3;
    private ImageView fotoPublicacao;
    private VideoView videoView;
    private ProgressDialog dialog;

    private String telaMode = "criando";
    private String tipo = "post";

    private boolean fotoUpload;
    private String fotoBytesId;
    private String fotoBytes_ContentType;

    private boolean videoUpload;
    private String videoBytesId;
    private byte[] videoBytes;
    private String videoBytes_ContentType;

    private boolean audioUpload;
    private String audioBytesId;
    private byte[] audioBytes;
    private String audioBytes_ContentType;

    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FindFM.setTelaAtual("CRIAR_POST");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_criar_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle param = getIntent().getBundleExtra("CriarPost");
        ImageView imageView = findViewById(R.id.circularImageView);
        if( param != null ) {
            if( !param.isEmpty() ) {
                telaMode = param.getString("telaMode");
            }
        } else {
            MidiaUtils.setImagemPerfilToImageView(imageView, this);
            binding.incluirContent.setPost(new Post().setAutor(new Usuario().setNomeCompleto(FindFM.getNomeUsuario(this))));
            //Somente contratante pode colocar titulo para o anuncio
            if(FindFM.getTipoUsuario(this) != TiposUsuario.CONTRATANTE){
                tipo = "ad";
                binding.incluirContent.txtTitulo.setVisibility(View.GONE);
            }
        }

        binding.executePendingBindings();

        fotoPublicacao = findViewById(R.id.fotoPublicacao);
        fotoPublicacao.setVisibility(View.GONE);

        videoView = findViewById(R.id.videoView);
        videoView.setVisibility(View.GONE);

        FloatingActionButton fab = findViewById(R.id.fab_foto);
        fab.setOnClickListener(view -> startActivityForResult(Intent.createChooser(MidiaUtils.pickImageIntent(), "Escolha uma foto"), PICK_IMAGE));

        FloatingActionButton video = findViewById(R.id.fab_video);
        video.setOnClickListener(view -> startActivityForResult(Intent.createChooser(MidiaUtils.pickVideoIntent(), "Escolha o video"), PICK_VIDEO));

        FloatingActionButton audio = findViewById(R.id.fab_audio);
        audio.setOnClickListener(view -> startActivityForResult(Intent.createChooser(MidiaUtils.pickAudioIntent(), "Escolha o arquivo de áudio"), PICK_AUDIO));


        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        if(telaMode.equals("criando")){
            binding.incluirContent.setPost(new Post().setAutor(FindFM.getUsuario()));
            MidiaUtils.setImagemPerfilToImageView(binding.incluirContent.circularImageView, this);
            //Somente contratante pode colocar titulo para o anuncio
            if(FindFM.getTipoUsuario(this) != TiposUsuario.CONTRATANTE){
                binding.incluirContent.txtTitulo.setVisibility(View.GONE);
            }
            else {
                binding.incluirContent.getPost().setTitulo("");
            }
        } else if(telaMode.equals("visualizar") || telaMode.equals("editavel")) {
            Post post = (Post) FindFM.getMap().get("post");
            binding.incluirContent.setPost(post);
            preencherTela(post);
        }

        if (telaMode.equals("editavel")){
            MidiaUtils.setImagemPerfilToImageView(binding.incluirContent.circularImageView, this);
        }

        binding.incluirContent.listaComentarios.setLayoutManager(new LinearLayoutManager(this));
        binding.incluirContent.listaComentarios.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    private void preencherTela(Post post){

        if(post.getFotoBytes() != null) {
            InputStream input = new ByteArrayInputStream(post.getFotoBytes());
            Bitmap ext_pic = BitmapFactory.decodeStream(input);
            binding.incluirContent.fotoPublicacao.setImageBitmap(ext_pic);
            binding.incluirContent.fotoPublicacao.setVisibility(View.VISIBLE);
            this.fotoBytes_ContentType = "image/jpeg";
        }

        if(post.getAutor().getFotoID() != null){

            DownloadResourceService downloadService = new DownloadResourceService(this);
            downloadService.addObserver( (download, arg) -> {
                if(download instanceof DownloadResourceService) {
                    runOnUiThread(() -> {
                        if (arg instanceof BinaryResponse) {
                            byte[] dados = ((BinaryResponse) arg).getData();
                            InputStream input=new ByteArrayInputStream(dados);
                            Bitmap ext_pic = BitmapFactory.decodeStream(input);
                            binding.incluirContent.circularImageView.setImageBitmap(ext_pic);
                            binding.incluirContent.scrollView2.scrollTo(0,0);
                        } else{
                            AlertDialogUtils.newSimpleDialog__OneButton(this,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id1) -> { }).create().show();
                        }

                        dialog.hide();


                    });
                }
            });
            downloadService.getResource(post.getAutor().getFotoID());
            dialog.setMessage("Carregando...");
            dialog.show();
        }

        for(String id : post.getIdVideos()){
            Uri uri = Uri.parse(HttpUtils.buildUrl(getResources(),"resource/" + id));
            FullScreenMediaController m = new FullScreenMediaController(this);
            m.setUrl(HttpUtils.buildUrl(getResources(),"resource/" + id));
            binding.incluirContent.videoView.setMediaController(m);
            m.setAnchorView(binding.incluirContent.videoView);
            binding.incluirContent.videoView.setVideoURI(uri);
            binding.incluirContent.videoView.setVisibility(View.VISIBLE);
            binding.incluirContent.scrollView2.scrollTo(0,0);
        }

        if(post.getIdAudio() != null) {
            try {
                Uri uri = Uri.parse(HttpUtils.buildUrl(getResources(), "resource/" + post.getIdAudio()));
                binding.incluirContent.frameAudio.setVisibility(View.VISIBLE);
                getFragmentManager().beginTransaction().replace(R.id.frame_audio,
                        new Audio_Fragment(this, uri))
                        .commit();
            } catch (Exception e){
                e.printStackTrace();
                AlertDialogUtils.newSimpleDialog__OneButton(this,
                        "Ops!", R.drawable.ic_error,
                        "Não foi possível obter a música." +
                                "\nVerifique sua conexão com a Internet e tente novamente","OK",
                        (dialog, id1) -> { }).create().show();
            }
        }

        if(post.getComentarios() != null){
            binding.incluirContent.listaComentarios.setAdapter(new AdapterComentario(post.getId(), post.getComentarios(), this));
        }

        if(post.getAutor().getTelefone() != null){
            binding.incluirContent.txtTelefone.setVisibility(View.VISIBLE);
        }


        Bundle bundle = new Bundle();
        View.OnClickListener irPerfil = v -> {
            if(post.getAutor().getId().equals(FindFM.getUsuario().getId())){
                bundle.putBoolean("euMesmo", true);
                Util.open_form_withParam__no_return(this, TelaPrincipal.class, "CriarPost", bundle);
            }else{
                bundle.putString("id_usuario", post.getAutor().getId());
                Util.open_form_withParam__no_return(this, TelaPrincipal.class, "CriarPost", bundle);
            }
        };


        binding.incluirContent.circularImageView.setOnClickListener(irPerfil);
        binding.incluirContent.txtNome.setOnClickListener(irPerfil);

        binding.incluirContent.txtTelefone.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+ post.getAutor().getTelefone().getTelefoneFormatado()));
            startActivity(callIntent);
        });

        binding.incluirContent.btnComentar.setOnClickListener(v -> {
            comentar(post);
        });


    }

    private void checkTelaMode(){
        if(telaMode.equals("visualizar") || telaMode.equals("editavel")){
            binding.incluirContent.txtTitulo.setEnabled(false);
            binding.incluirContent.txtDesc.setEnabled(false);

            binding.fabFoto.setVisibility(View.INVISIBLE);
            binding.fabVideo.setVisibility(View.INVISIBLE);
            binding.fabAudio.setVisibility(View.INVISIBLE);

            binding.incluirContent.btnRemoverImagem.setVisibility(View.GONE);
            binding.incluirContent.btnRemoverVideo.setVisibility(View.GONE);
            binding.incluirContent.btnRemoverAudio.setVisibility(View.GONE);
            optionsMenu.getItem(0).setVisible(false);
        } else if (telaMode.equals("editando") || telaMode.equals("criando")){
            if(optionsMenu != null) {
                optionsMenu.getItem(0).setVisible(true);
            }
            binding.incluirContent.txtTitulo.setEnabled(true);
            binding.incluirContent.txtDesc.setEnabled(true);
            binding.incluirContent.txtTelefone.setVisibility(View.GONE);
            binding.incluirContent.textView5.setVisibility(View.GONE);
            binding.incluirContent.listaComentarios.setVisibility(View.GONE);
            binding.incluirContent.txtComentar.setVisibility(View.GONE);
            binding.incluirContent.btnComentar.setVisibility(View.GONE);

            if(binding.incluirContent.getPost().getFotoBytes() == null) {
                binding.incluirContent.btnRemoverImagem.setVisibility(View.GONE);
                binding.fabFoto.setVisibility(View.VISIBLE);
            } else {
                binding.incluirContent.btnRemoverImagem.setVisibility(View.VISIBLE);
                binding.fabFoto.setVisibility(View.GONE);
            }

            if(videoBytes == null) {
                binding.incluirContent.btnRemoverVideo.setVisibility(View.GONE);
                binding.fabVideo.setVisibility(View.VISIBLE);
            } else {
                binding.incluirContent.btnRemoverVideo.setVisibility(View.VISIBLE);
                binding.fabVideo.setVisibility(View.GONE);
            }

            if(audioBytes == null) {
                binding.incluirContent.btnRemoverAudio.setVisibility(View.GONE);
                binding.fabAudio.setVisibility(View.VISIBLE);
            } else {
                binding.incluirContent.btnRemoverAudio.setVisibility(View.VISIBLE);
                binding.fabAudio.setVisibility(View.GONE);
            }
        }

        if(telaMode.equals("editavel") && optionsMenu != null){
            optionsMenu.getItem(0).setVisible(false);
        }

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
        this.optionsMenu = menu;
        checkTelaMode();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.hideSoftKeyboard(this);
        switch (item.getItemId()){
            case R.id.action_salvar:
                if(binding.incluirContent.txtDesc.getText().toString().isEmpty() ){
                    Toast.makeText(this, "Escreva algo em sua publicação...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                UploadResourceService resourceService = new UploadResourceService(this);
                resourceService.addObserver(this);
                dialog.setMessage("Publicando, aguarde...");
                this.dialog.show();
                Toast.makeText(this, "Salvando post...", Toast.LENGTH_SHORT).show();
                if(binding.incluirContent.getPost().getFotoBytes() != null) {
                    fotoUpload = true;
                    resourceService.uploadFiles(binding.incluirContent.getPost().getFotoBytes(), fotoBytes_ContentType, "foto");
                }
                if(videoBytes != null) {
                    videoUpload = true;
                    resourceService.uploadFiles(videoBytes, videoBytes_ContentType, "video");
                }
                if(audioBytes != null) {
                    audioUpload = true;
                    resourceService.uploadFiles(audioBytes, audioBytes_ContentType, "audio");
                }

                if(!fotoUpload && !videoUpload && !audioUpload){
                    initRequest(binding.incluirContent.getPost());
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO: ADICIONAR REGRAS DE DURAÇÃO DE VIDEO E AUDIO MAIOR QUE 15S -> MODAL "Sim isso é de autoria propria"
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getApplicationContext()
                        .getContentResolver().openInputStream(Objects.requireNonNull(data.getData())));
                this.fotoPublicacao.setImageBitmap(bitmap);
                this.fotoPublicacao.setVisibility(View.VISIBLE);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                binding.incluirContent.getPost().setFotoBytes(stream.toByteArray());
                this.fotoBytes_ContentType = "image/jpeg";
                checkTelaMode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_VIDEO && resultCode == Activity.RESULT_OK && data != null) {
            Uri u = data.getData();
            MediaPlayer mp = MediaPlayer.create(this, u);
            int duration = mp.getDuration();
            long duracaoSegundos = TimeUnit.MILLISECONDS.toSeconds(duration);

            if(duracaoSegundos > 15L) {
                AlertDialogUtils.newSimpleDialog__TwoButtons(this, "Atenção!", R.drawable.ic_error, "Esse arquivo tem duração maior do que 15 segundos\n" +
                                "Só permitimos postar arquivos de vídeo maiores do que 15 segundos se estes forem de sua autoria.\nEsse conteúdo é de sua autoria?",
                        "Sim, esse vídeo é de minha autoria", "Não, esse vídeo não é de minha autoria",
                        (dialogInterface, i) -> {
                            setVideo(u);
                        },
                        (dialogInterface, i) -> {

                        });
            } else {
                setVideo(u);
            }
        }
        if (requestCode == PICK_AUDIO && resultCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();

            MediaPlayer mp = MediaPlayer.create(this, uri);
            int duration = mp.getDuration();
            long duracaoSegundos = TimeUnit.MILLISECONDS.toSeconds(duration);

            if(duracaoSegundos > 15L){
                AlertDialogUtils.newSimpleDialog__TwoButtons(this, "Atenção!", R.drawable.ic_error, "Esse arquivo tem duração maior do que 15 segundos\n" +
                                "Só permitimos postar arquivos de aúdio maiores do que 15 segundos se estes forem de sua autoria.\nEsse conteúdo é de sua autoria?",
                        "Sim, esse áudio é de minha autoria", "Não, esse áudio não é de minha autoria",
                        (dialogInterface, i) -> {
                            setAudio(uri);
                        },
                        (dialogInterface, i) -> {

                        });
            } else {
                setAudio(uri);
            }

        }
    }

    private void setVideo(Uri u){
        try {
            videoView.setMediaController(new MediaController(this));
            videoView.setVideoURI(u);
            videoView.setVisibility(View.VISIBLE);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream fis = getContentResolver().openInputStream(u);

            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);

            this.videoBytes = baos.toByteArray();
            this.videoBytes_ContentType = "video/" + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(u));
            checkTelaMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAudio(Uri uri){
        try {
            binding.incluirContent.frameAudio.setVisibility(View.VISIBLE);
            getFragmentManager().beginTransaction().replace(R.id.frame_audio,
                    new Audio_Fragment(this, uri))
                    .commit();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream fis = getContentResolver().openInputStream(uri);

            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);

            this.audioBytes = baos.toByteArray();
            this.audioBytes_ContentType = "audio/" + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
            checkTelaMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnRemoverImagem_Click(View v){
        fotoUpload = false;
        fotoBytesId = null;
        binding.incluirContent.getPost().setFotoBytes(null);
        fotoBytes_ContentType = null;
        fotoPublicacao.setVisibility(View.GONE);
        checkTelaMode();
    }

    public void btnRemoverVideo_Click(View v){
        videoUpload = false;
        videoBytesId = null;
        videoBytes = null;
        videoBytes_ContentType = null;
        videoView.setVisibility(View.GONE);
        checkTelaMode();
    }

    public void btnRemoverAudio_Click(View v){
        audioUpload = false;
        audioBytesId = null;
        audioBytes = null;
        audioBytes_ContentType= null;
        getSupportFragmentManager().beginTransaction().
                remove(getSupportFragmentManager().findFragmentById(R.id.frame_audio)).commit();
        binding.incluirContent.frameAudio.setVisibility(View.GONE);
        checkTelaMode();
    }

    private void initRequest(Post post){
        JsonTypedRequest<PostRequest, ResponseBody, ErrorResponse> postRequest = new JsonTypedRequest<>(
                this,
                HttpMethod.POST.getCodigo(),
                PostRequest.class,
                ResponseBody.class,
                ErrorResponse.class,
                HttpUtils.buildUrl(getResources(),tipo, "create"),
                null,
                (ResponseBody response) -> {
                    this.dialog.hide();
                    if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                        AlertDialogUtils.newSimpleDialog__OneButton(this,
                                "Sucesso!", R.drawable.ic_error,
                                "Post cadastrado com sucesso","OK",
                                (dialog, id) -> {
                                    dialog.dismiss();
                                    FindFM.setTelaAtual("POST_CRIADO");
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
                (VolleyError error) -> {
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
                .setVideoId(post.getIdVideo())
                .setAudioId(post.getIdAudio());
        postRequest.setRequest(param);
        dialog.setMessage("Publicando, aguarde...");
        dialog.show();
        postRequest.execute();
    }

    private void comentar(Post post){
        JsonTypedRequest<ComentarRequest, ResponseBody, ErrorResponse> postRequest = new JsonTypedRequest<>(
                this,
                HttpMethod.POST.getCodigo(),
                ComentarRequest.class,
                ResponseBody.class,
                ErrorResponse.class,
                HttpUtils.buildUrl(getResources(),"post/comment/" + post.getId()),
                null,
                (ResponseBody response) -> {
                    this.dialog.hide();
                    if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                        AlertDialogUtils.newSimpleDialog__OneButton(this,
                                "Sucesso!", R.drawable.ic_error,
                                "Comentário cadastrado com sucesso","OK",
                                (dialog, id) -> {
                                    //TODO: get do post
                                    dialog.dismiss();
                                    FindFM.setTelaAtual("POST_CRIADO");
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
                (VolleyError error) -> {
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
        postRequest.setRequest(new ComentarRequest(binding.incluirContent.txtComentar.getText().toString()));
        dialog.setMessage("Publicando, aguarde...");
        dialog.show();
        postRequest.execute();
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
                    else if(result.equals("video"))
                        videoUpload = false;
                    else
                        audioUpload = false;
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
                    else if(result.equals("video"))
                        videoUpload = false;
                    else
                        audioUpload = false;

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
                    } else if (resultados[0].equals("audio")){
                        audioUpload = false;
                        audioBytesId = resultados[1];
                        binding.incluirContent.getPost().setIdAudio(audioBytesId);
                    }

                    if(!videoUpload && !fotoUpload && !audioUpload){
                        initRequest(binding.incluirContent.getPost());
                    }
                }
            });
        }
    }
}
