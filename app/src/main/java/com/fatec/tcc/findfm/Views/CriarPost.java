package com.fatec.tcc.findfm.Views;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.DownloadResourceService;
import com.fatec.tcc.findfm.Infrastructure.Request.UploadResourceService;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.FileReference;
import com.fatec.tcc.findfm.Model.Business.Post;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Request.ComentarRequest;
import com.fatec.tcc.findfm.Model.Http.Request.PostRequest;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.FeedResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpMethod;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.MidiaUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Adapters.AdapterComentario;
import com.fatec.tcc.findfm.databinding.ActivityCriarPostBinding;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class CriarPost extends AppCompatActivity implements Observer{

    private ActivityCriarPostBinding binding;
    private Address localizacaoAtual;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int PICK_AUDIO = 3;

    private ProgressDialog dialog;

    private String telaMode = "criando";
    private String tipo = "post";

    private List<FileReference> filesToUpload = new ArrayList<>();

    private boolean isVisitante;
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
                isVisitante = param.getBoolean("visitante");
            }
        } else {
            MidiaUtils.setImagemPerfilToImageView(imageView, this);
            binding.incluirContent.setPost(
                    new Post()
                            .setAutor(new Usuario().setNomeCompleto(FindFM.getNomeUsuario(this)).setTipoUsuario(FindFM.getTipoUsuario(this)))
                            .setMidias(new ArrayList<>()));
            if(FindFM.getTipoUsuario(this) != TiposUsuario.CONTRATANTE){
                binding.incluirContent.txtTitulo.setVisibility(View.GONE);
            } else {
                binding.incluirContent.txtTitulo.setVisibility(View.VISIBLE);
                binding.incluirContent.getPost().setTitulo("");
                tipo = "ad";

            }
        }

        binding.executePendingBindings();

        binding.incluirContent.fotoPublicacao.setVisibility(View.GONE);
        binding.incluirContent.videoView.setVisibility(View.GONE);

        FloatingActionButton fab = findViewById(R.id.fab_foto);
        fab.setOnClickListener(view -> startActivityForResult(Intent.createChooser(MidiaUtils.pickImageIntent(), "Escolha uma foto"), PICK_IMAGE));

        FloatingActionButton video = findViewById(R.id.fab_video);
        video.setOnClickListener(view -> startActivityForResult(Intent.createChooser(MidiaUtils.pickVideoIntent(), "Escolha o video"), PICK_VIDEO));

        FloatingActionButton audio = findViewById(R.id.fab_audio);
        audio.setOnClickListener(view -> startActivityForResult(Intent.createChooser(MidiaUtils.pickAudioIntent(), "Escolha o arquivo de áudio"), PICK_AUDIO));


        dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        if(telaMode.equals("criando")){
            binding.incluirContent.setPost(
                    new Post()
                            .setAutor(FindFM.getUsuario())
                            .setMidias(new ArrayList<>()));
            MidiaUtils.setImagemPerfilToImageView(binding.incluirContent.circularImageView, this);
            if(!TiposUsuario.CONTRATANTE.equals(FindFM.getTipoUsuario(this))){
                binding.incluirContent.txtTitulo.setVisibility(View.GONE);
            }
            else {
                binding.incluirContent.txtTitulo.setVisibility(View.VISIBLE);
                binding.incluirContent.getPost().setTitulo("");
                tipo = "ad";
            }

            if(TiposUsuario.MUSICO.equals(binding.incluirContent.getPost().getAutor().getTipoUsuario())) {
                try {
                    getSupportActionBar().setTitle("Publicação");
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                try {
                    getSupportActionBar().setTitle("Anúncio");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }


        } else if(telaMode.equals("visualizar") || telaMode.equals("editavel")) {
            Post post = (Post) FindFM.getMap().get("post");
            if(TiposUsuario.CONTRATANTE.equals(post.getAutor().getTipoUsuario()))
                tipo = "ad";

            binding.incluirContent.setPost(post);
            getPost();
        }

        if (telaMode.equals("editavel")){
            MidiaUtils.setImagemPerfilToImageView(binding.incluirContent.circularImageView, this);
        }

        binding.incluirContent.listaComentarios.setLayoutManager(new LinearLayoutManager(this));
        binding.incluirContent.listaComentarios.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        getCidade();
    }

    private void preencherTela(Post post){
        for(FileReference midia : post.getMidias()) {

            if(midia.getContentType().contains("img")) {
                DownloadResourceService downloadService = new DownloadResourceService(this);
                downloadService.addObserver((download, arg) -> {
                    if (download instanceof DownloadResourceService) {
                        runOnUiThread(() -> {
                            if (arg instanceof BinaryResponse) {
                                byte[] dados = ((BinaryResponse) arg).getData();
                                InputStream input = new ByteArrayInputStream(dados);
                                Bitmap ext_pic = BitmapFactory.decodeStream(input);
                                binding.incluirContent.fotoPublicacao.setImageBitmap(ext_pic);
                                binding.incluirContent.fotoPublicacao.setVisibility(View.VISIBLE);
                            } else {
                                AlertDialogUtils.newSimpleDialog__OneButton(this,
                                        "Ops!", R.drawable.ic_error,
                                        "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                                "\nVerifique sua conexão com a Internet e tente novamente", "OK",
                                        (dialog, id1) -> {
                                        }).create().show();
                            }

                            dialog.hide();

                        });
                    }
                });
                downloadService.getResource(midia.getId());
                dialog.show();
            }

            if(midia.getContentType().contains("vid")) {
                try {
                    Uri uri = Uri.parse(HttpUtils.buildUrl(getResources(), "resource/" + midia.getId()));

                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                    SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
                    DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                    MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
                    binding.incluirContent.videoView.setPlayer(exoPlayer);
                    exoPlayer.prepare(mediaSource);
                    exoPlayer.seekTo(100);
                    binding.incluirContent.videoView.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(midia.getContentType().contains("mus")) {
                try {
                    Uri uri = Uri.parse(HttpUtils.buildUrl(getResources(), "resource/" + midia.getId()));
                    binding.incluirContent.frameAudio.setVisibility(View.VISIBLE);
                    getFragmentManager().beginTransaction().replace(R.id.frame_audio,
                            new Audio_Fragment(this, uri))
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Ops!", R.drawable.ic_error,
                            "Não foi possível obter a música." +
                                    "\nVerifique sua conexão com a Internet e tente novamente", "OK",
                            (dialog, id1) -> {
                            }).create().show();
                }
            }
        }

        if(post.getCidade() != null && post.getUf() != null)
            binding.incluirContent.txtLocalizacao.setText(post.getLocalizacaoFormatada());

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

        if(post.getComentarios() == null || post.getComentarios().isEmpty()){
            binding.incluirContent.listaComentarios.setVisibility(View.GONE);
        } else {
            binding.incluirContent.listaComentarios.setVisibility(View.VISIBLE);
            binding.incluirContent.listaComentarios.setAdapter(new AdapterComentario(post.getId(), post.getComentarios(), this, isVisitante));

        }

        if(post.getAutor().getTelefone() != null){
            binding.incluirContent.txtTelefone.setVisibility(View.VISIBLE);
        } else {
            binding.incluirContent.txtTelefone.setVisibility(View.GONE);
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
        if(isVisitante){
            Activity activity = this;
            binding.incluirContent.btnComentar.setOnClickListener(view -> AlertDialogUtils.newSimpleDialog__OneButton(activity,
                    "Ops!", R.drawable.ic_error,
                    "Essa ação requer que você esteja logado com uma conta\nLogue ou crie uma conta","OK",
                    (dialog, id) -> { }).create().show());
        }

        if(TiposUsuario.MUSICO.equals(binding.incluirContent.getPost().getAutor().getTipoUsuario())) {
            try {
                getSupportActionBar().setTitle("Publicação");
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            try {
                getSupportActionBar().setTitle("Anúncio");
            } catch (Exception e){
                e.printStackTrace();
            }
        }

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
            optionsMenu.getItem(1).setVisible(true);
        } else if (telaMode.equals("editando") || telaMode.equals("criando")){
            if(optionsMenu != null) {
                optionsMenu.getItem(0).setVisible(true);
                optionsMenu.getItem(1).setVisible(false);
            }
            binding.incluirContent.txtTitulo.setEnabled(true);
            binding.incluirContent.txtDesc.setEnabled(true);
            //binding.incluirContent.txtTelefone.setVisibility(View.GONE);
            binding.incluirContent.textView5.setVisibility(View.GONE);
            binding.incluirContent.listaComentarios.setVisibility(View.GONE);
            binding.incluirContent.txtComentar.setVisibility(View.GONE);
            binding.incluirContent.btnComentar.setVisibility(View.GONE);

            for(FileReference midia : filesToUpload) {

                if(midia.getContentType().contains("img")) {
                    binding.incluirContent.btnRemoverImagem.setVisibility(View.VISIBLE);
                    binding.fabFoto.setVisibility(View.GONE);
                }

                if(midia.getContentType().contains("vid")) {
                    binding.incluirContent.btnRemoverVideo.setVisibility(View.VISIBLE);
                    binding.fabVideo.setVisibility(View.GONE);
                }

                if(midia.getContentType().contains("mus")) {
                    binding.incluirContent.btnRemoverAudio.setVisibility(View.VISIBLE);
                    binding.fabAudio.setVisibility(View.GONE);
                }
            }
        }

        if(telaMode.equals("criando")) {
            binding.incluirContent.txtData.setVisibility(View.GONE);
        }

        if(telaMode.equals("editavel") && optionsMenu != null){
            optionsMenu.getItem(0).setVisible(false);
        }

        //Diferenciar post de anuncio
        if (binding.incluirContent.getPost().getTitulo() != null) {
            binding.incluirContent.textView5.setVisibility(View.GONE);
            binding.incluirContent.txtComentar.setVisibility(View.GONE);
            binding.incluirContent.listaComentarios.setVisibility(View.GONE);
            binding.incluirContent.btnComentar.setVisibility(View.GONE);
        }


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

    private void getPost(){
        JsonTypedRequest<Usuario, ResponseBody, ErrorResponse> getPost = new JsonTypedRequest<>
                (       this,
                        Request.Method.GET,
                        Usuario.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        HttpUtils.buildUrl(getResources(),tipo, binding.incluirContent.getPost().getId() ),
                        null,
                        (ResponseBody response) ->
                        {
                            dialog.hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                FeedResponse feedResponse = JsonUtils.jsonConvert(response.getData(), FeedResponse.class);
                                binding.incluirContent.setPost(new Post(feedResponse));
                                binding.incluirContent.executePendingBindings();
                                checkTelaMode();
                                preencherTela(binding.incluirContent.getPost());
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            dialog.hide();
                            AlertDialogUtils.newSimpleDialog__OneButton(this,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(),"OK",
                                    (dialog, id) -> { }).create().show();
                        },
                        (VolleyError error) ->
                        {
                            dialog.hide();
                            error.printStackTrace();
                            AlertDialogUtils.newSimpleDialog__OneButton(this,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id) -> { }).create().show();
                        }
                );
        try {
            getFragmentManager().findFragmentById(R.id.frame_audio).onDestroy();
        }
        catch (Exception e){}
        getPost.execute();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.hideSoftKeyboard(this);
        switch (item.getItemId()){
            case R.id.action_salvar:
                if(binding.incluirContent.txtDesc.getText().toString().isEmpty() ){
                    Toast.makeText(this, "Adicione um texto...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                UploadResourceService resourceService = new UploadResourceService(this);
                resourceService.addObserver(this);
                dialog.setMessage("Publicando, aguarde...");
                this.dialog.show();

                for(FileReference midia : filesToUpload) {
                    resourceService.uploadFiles(midia.getConteudo(), midia.getContentType());
                }
                if(filesToUpload.isEmpty()){
                    initRequest(binding.incluirContent.getPost());
                }
                break;
            case R.id.action_refresh:
                getPost();
                break;
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
                binding.incluirContent.fotoPublicacao.setImageBitmap(bitmap);
                binding.incluirContent.fotoPublicacao.setVisibility(View.VISIBLE);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                filesToUpload.add( new FileReference()
                        .setContentType("img/jpeg")
                        .setConteudo(stream.toByteArray())
                );
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
                AlertDialogUtils.newSimpleDialog__TwoButtons(this, "Atenção!", R.drawable.ic_error, "Este vídeo tem duração maior do que 15 segundos!\n" +
                                "Só permitimos postar vídeos maiores do que 15 segundos se as músicas nos mesmos forem de sua autoria.\nAs músicas deste vídeo são de sua autoria?",
                        "Sim, as músicas são de minha autoria.", "Não, as músicas não são de minha autoria.",
                        (dialogInterface, i) -> setVideo(u),
                        (dialogInterface, i) -> {

                        }).show();
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
                AlertDialogUtils.newSimpleDialog__TwoButtons(this, "Atenção!", R.drawable.ic_error, "Este arquivo de áudio tem duração maior do que 15 segundos!\n" +
                                "Só permitimos postar arquivos de áudio maiores do que 15 segundos se as músicas nos mesmos forem de sua autoria.\nAs músicas deste arquivo de áudio são de sua autoria?",
                        "Sim, as músicas são de minha autoria.", "Não, as músicas não são de minha autoria.",
                        (dialogInterface, i) -> setAudio(uri),
                        (dialogInterface, i) -> { }).show();
            } else {
                setAudio(uri);
            }

        }
    }

    private void setVideo(Uri u){
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                    this, "exoplayer_video");
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .setExtractorsFactory(new DefaultExtractorsFactory())
                    .createMediaSource(u);
            exoPlayer.prepare(mediaSource);
            binding.incluirContent.videoView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.seekTo(100);
            binding.incluirContent.videoView.setVisibility(View.VISIBLE);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream fis = getContentResolver().openInputStream(u);

            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);

            filesToUpload.add( new FileReference()
                    .setContentType("video/" + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(u)))
                    .setConteudo(baos.toByteArray())
            );
            checkTelaMode();
        } catch (Exception e){
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

            filesToUpload.add( new FileReference()
                    .setContentType("mus/" + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri)))
                    .setConteudo(baos.toByteArray())
            );
            checkTelaMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnRemoverImagem_Click(View v){
        List<FileReference> filesToRemove = new ArrayList<>();
        for(FileReference midia : filesToUpload){
            if(midia.getContentType().contains("img")){
                filesToRemove.add(midia);
                binding.incluirContent.fotoPublicacao.setVisibility(View.GONE);
                binding.incluirContent.btnRemoverImagem.setVisibility(View.GONE);
                binding.fabFoto.setVisibility(View.VISIBLE);
            }
        }
        filesToUpload.removeAll(filesToRemove);
        checkTelaMode();
    }

    public void btnRemoverVideo_Click(View v){
        List<FileReference> filesToRemove = new ArrayList<>();
        for(FileReference midia : filesToUpload){
            if(midia.getContentType().contains("vid")){
                filesToRemove.add(midia);
                binding.incluirContent.videoView.setVisibility(View.GONE);
                binding.incluirContent.btnRemoverVideo.setVisibility(View.GONE);
                binding.fabVideo.setVisibility(View.VISIBLE);
            }
        }
        filesToUpload.removeAll(filesToRemove);
        checkTelaMode();
    }

    public void btnRemoverAudio_Click(View v){
        List<FileReference> filesToRemove = new ArrayList<>();
        for(FileReference midia : filesToUpload){
            if(midia.getContentType().contains("mus")) {
                filesToRemove.add(midia);
                try {
                    getFragmentManager().findFragmentById(R.id.frame_audio).onDestroy();
                }
                catch (Exception e){}
                binding.incluirContent.frameAudio.setVisibility(View.GONE);
                binding.incluirContent.btnRemoverAudio.setVisibility(View.GONE);
                binding.fabAudio.setVisibility(View.VISIBLE);
            }
        }
        filesToUpload.removeAll(filesToRemove);
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
                                "Sucesso!", R.drawable.ic_save,
                                "Publicado com sucesso!","OK",
                                (dialog, id) -> {
                                    dialog.dismiss();
                                    FindFM.setTelaAtual("POST_CRIADO");
                                    binding.incluirContent.setPost(
                                            new Post()
                                                    .setId(response.getData().toString())
                                                    .setMidias(new ArrayList<>()));
                                    telaMode = "visualizar";
                                    isVisitante = false;
                                    getPost(); }).create().show();

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
                .setMidias(post.getMidias());
        postRequest.setRequest(param);
        try {
            getFragmentManager().findFragmentById(R.id.frame_audio).onDestroy();
        }
        catch (Exception e){}
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
                                    binding.incluirContent.txtComentar.setText("");
                                    this.dialog.setMessage("Carregando...");
                                    FindFM.setTelaAtual("POST_CRIADO");
                                    getPost(); }).create().show();

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
        postRequest.setRequest(new ComentarRequest(binding.incluirContent.txtComentar.getText().toString()));
        try {
            getFragmentManager().findFragmentById(R.id.frame_audio).onDestroy();
        }
        catch (Exception e){}
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
        if(upload instanceof UploadResourceService){
            runOnUiThread(() -> {
                if(arg instanceof ErrorResponse){
                    ErrorResponse error = (ErrorResponse) arg;
                    dialog.hide();

                    String result = (String) arg;

                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Ops!", R.drawable.ic_error,
                            error.getMessage(),"Tentar novamente",
                            (dialog, id) -> {
                                UploadResourceService resourceService = new UploadResourceService(this);
                                resourceService.addObserver(this);
                                this.dialog.setMessage("Publicando, aguarde...");
                                this.dialog.show();

                                for(FileReference midia : filesToUpload) {
                                    if(midia.getContentType().equals(result)) {
                                        resourceService.uploadFiles(midia.getConteudo(), midia.getContentType());
                                    }
                                }
                            }).create().show();
                } else if (arg instanceof Exception){
                    Exception error = (Exception) arg;
                    dialog.hide();
                    error.printStackTrace();
                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Ops!", R.drawable.ic_error,
                            "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                    "\nVerifique sua conexão com a Internet e tente novamente","OK",
                            (dialog, id) -> {
                            }).create().show();
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

                    binding.incluirContent.getPost()
                            .getMidias().add(new FileReference()
                            .setId(resultados[1])
                            .setContentType(resultados[0]));

                    List<FileReference> filesToRemove = new ArrayList<>();
                    for(FileReference midia : filesToUpload){
                        if(midia.getContentType().equals(resultados[0])){
                            filesToRemove.add(midia);
                        }
                    }
                    filesToUpload.removeAll(filesToRemove);

                    if(filesToUpload.isEmpty()){
                        initRequest(binding.incluirContent.getPost());
                    }
                }
            });
        }
    }

    public boolean getCidade() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialogUtils.newSimpleDialog__OneButton(this, "Atenção!", R.drawable.ic_error,
                        R.string.texto_localizacao_permissao, "OK", (dialogInterface, i) -> {
                            ActivityCompat.requestPermissions(CriarPost.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        });
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            Activity activity = this;
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            localizacaoAtual = Util.getLocalizacao(activity, location.getLatitude(), location.getLongitude());
                            //TODO: Gravar no FindFM essas informações
                        }
                    });
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                        Activity activity = this;
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(this, location -> {
                                    if (location != null) {
                                        localizacaoAtual = Util.getLocalizacao(activity, location.getLatitude(), location.getLongitude());
                                        //TODO: Gravar no FindFM essas informações
                                    }
                                });
                    }

                } else {
                    AlertDialogUtils.newSimpleDialog__OneButton(this, "Atenção!", R.drawable.ic_error,
                            R.string.texto_localizacao_sem_permissao, "OK", (dialogInterface, i) -> {
                                ActivityCompat.requestPermissions(CriarPost.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            });
                }
                return;
            }

        }
    }
}
