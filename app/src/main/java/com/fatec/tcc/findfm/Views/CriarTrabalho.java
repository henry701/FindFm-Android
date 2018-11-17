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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.DownloadResourceService;
import com.fatec.tcc.findfm.Infrastructure.Request.UploadResourceService;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.FileReference;
import com.fatec.tcc.findfm.Model.Business.Musica;
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Business.Trabalho;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Request.Denuncia;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
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
import com.fatec.tcc.findfm.Views.Adapters.AdapterMusica;
import com.fatec.tcc.findfm.Views.Adapters.AdapterUsuario;
import com.fatec.tcc.findfm.databinding.ActivityCriarTrabalhoBinding;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CriarTrabalho extends AppCompatActivity implements Observer {

    private ActivityCriarTrabalhoBinding binding;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int PICK_AUDIO = 3;
    private ProgressDialog dialog;
    private boolean isVisitante;
    private String telaMode = "criando";

    private List<FileReference> filesToUpload = new ArrayList<>();
    private List<Musica> listaMusicas = new ArrayList<>();
    private List<String> musicasEnviadas = new ArrayList<>();
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FindFM.setTelaAtual("CRIAR_TRABALHO");
        try {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_criar_trabalho);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            Bundle param = getIntent().getBundleExtra("CriarTrabalho");
            if (param != null) {
                if (!param.isEmpty()) {
                    telaMode = param.getString("telaMode");
                    isVisitante = param.getBoolean("visitante");
                }
            }

            if (telaMode.equals("criando")) {
                List<Musico> musicos = new ArrayList<>();
                musicos.add(FindFM.getMusico());
                binding.incluirContent.setTrabalho(
                        new Trabalho()
                                .setMusicos(musicos)
                                .setMidias(new ArrayList<>())
                                .setMusicas(new ArrayList<>()));
                getSupportActionBar().setTitle("Novo Trabalho");
            } else if (telaMode.equals("visualizar")) {
                Trabalho trabalho = (Trabalho) FindFM.getMap().get("trabalho");
                binding.incluirContent.setTrabalho(trabalho);
                getTrabalho();
                getSupportActionBar().setTitle(binding.incluirContent.getTrabalho().getNome());
            }

        } catch (Exception e){
            e.printStackTrace();
        }


        binding.executePendingBindings();

        binding.incluirContent.fotoPublicacao.setVisibility(View.GONE);
        binding.incluirContent.videoView.setVisibility(View.GONE);

        FloatingActionButton fab = findViewById(R.id.fab_foto);
        fab.setOnClickListener(view -> startActivityForResult(Intent.createChooser(MidiaUtils.pickImageIntent(), "Escolha uma foto"), PICK_IMAGE));

        FloatingActionButton video = findViewById(R.id.fab_video);
        video.setOnClickListener(view -> startActivityForResult(Intent.createChooser(MidiaUtils.pickVideoIntent(), "Escolha o video"), PICK_VIDEO));

        binding.incluirContent.listaMusicas.setLayoutManager(new LinearLayoutManager(this));
        binding.incluirContent.btnAdicionarMusica.setOnClickListener(v -> startActivityForResult(Intent.createChooser(MidiaUtils.pickAudioIntent(), "Escolha a música"), PICK_AUDIO));
        binding.incluirContent.btnAdicionarPessoa.setOnClickListener(v -> Util.open_form(getApplicationContext(), SearchUsuario.class));
        dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
    }

    private void preencherTela(Trabalho trabalho) {

        for(FileReference midia : trabalho.getMidias()) {

            if(midia.getContentType().contains("img") && midia.getId() != null) {
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
                                binding.incluirContent.fotoPublicacao.setOnLongClickListener(v -> {
                                    denunciar("Imagem", midia.getId());
                                    return true;
                                });
                            } else {
                                Log.e("[ERRO-Download]IMG", "Erro ao baixar binário da imagem");
                                AlertDialogUtils.newSimpleDialog__OneButton(this,
                                        "Ops!", R.drawable.ic_error,
                                        "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                                "\nVerifique sua conexão com a Internet e tente novamente.", "OK",
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

            if(midia.getContentType().contains("vid") && midia.getId() != null) {
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
                    if(!isVisitante) {
                        binding.incluirContent.videoView.setOnLongClickListener(v -> {
                            denunciar("Vídeo", midia.getId());
                            return true;
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for(Musica musica : trabalho.getMusicas()){
            listaMusicas = new ArrayList<>();

            boolean jaContem = false;
            for(int i = 0; i < listaMusicas.size(); i++) {
                if(listaMusicas.get(i).equals(musica)) {
                    jaContem = true;
                }
            }

            if(!jaContem) {
                listaMusicas.add(musica);
            }

            binding.incluirContent.listaMusicas.setVisibility(View.VISIBLE);
            if (binding.incluirContent.listaMusicas.getAdapter() != null && binding.incluirContent.listaMusicas.getAdapter() instanceof AdapterMusica){
                ((AdapterMusica) binding.incluirContent.listaMusicas.getAdapter()).stopMedia();
            }
            binding.incluirContent.listaMusicas.setAdapter( new AdapterMusica(listaMusicas, this, "criando".equals(telaMode), TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario())));
        }

        binding.incluirContent.checkOriginal.setChecked(trabalho.isOriginal());
        if(!telaMode.equals("criando"))
            binding.incluirContent.checkOriginal.setEnabled(false);

        if (trabalho.getMusicos() == null) {
            binding.incluirContent.lbParticipantes.setText(R.string.sem_integrantes);
            binding.incluirContent.listaPessoas.setVisibility(View.GONE);
        } else {
            binding.incluirContent.lbParticipantes.setText(R.string.integrantes);
            binding.incluirContent.listaPessoas.setLayoutManager(new LinearLayoutManager(this));
            binding.incluirContent.listaPessoas.addItemDecoration(
                    new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            binding.incluirContent.listaPessoas.setAdapter(new AdapterUsuario(new HashSet<>(trabalho.getMusicos()), this, false));
        }

    }

    private void checkTelaMode() {
        if (telaMode.equals("visualizar")) {
            binding.incluirContent.txtTitulo.setEnabled(false);
            binding.incluirContent.txtDesc.setEnabled(false);
            binding.incluirContent.btnDenunciar.setVisibility(View.INVISIBLE);
            binding.fabFoto.setVisibility(View.INVISIBLE);
            binding.fabVideo.setVisibility(View.INVISIBLE);
            binding.incluirContent.checkOriginal.setEnabled(false);
            binding.incluirContent.btnRemoverImagem.setVisibility(View.GONE);
            binding.incluirContent.btnRemoverVideo.setVisibility(View.GONE);
            binding.incluirContent.btnAdicionarMusica.setVisibility(View.GONE);
            binding.incluirContent.btnAdicionarPessoa.setVisibility(View.GONE);
            optionsMenu.getItem(0).setVisible(false);
            optionsMenu.getItem(1).setVisible(true);
            if(!isVisitante) {
                optionsMenu.getItem(2).setVisible(true);
            } else {
                optionsMenu.getItem(2).setVisible(false);
            }
            try {
                getSupportActionBar().setTitle("Trabalho");
            } catch (Exception e){
                e.printStackTrace();
            }

        } else if (telaMode.equals("criando")) {
            try {
                getSupportActionBar().setTitle("Novo Trabalho");
            } catch (Exception e){
                e.printStackTrace();
            }
            if (optionsMenu != null) {
                optionsMenu.getItem(0).setVisible(true);
                optionsMenu.getItem(1).setVisible(false);
                optionsMenu.getItem(2).setVisible(false);
            }
            binding.incluirContent.btnDenunciar.setVisibility(View.VISIBLE);
            binding.incluirContent.checkOriginal.setOnClickListener(view -> {
                if(binding.incluirContent.checkOriginal.isChecked()) {
                    binding.incluirContent.checkOriginal.setChecked(false);
                    binding.incluirContent.getTrabalho().setOriginal(false);
                } else {
                    binding.incluirContent.checkOriginal.setChecked(true);
                    binding.incluirContent.getTrabalho().setOriginal(true);
                }
            });
            binding.incluirContent.txtTitulo.setEnabled(true);
            binding.incluirContent.txtDesc.setEnabled(true);

            for(FileReference midia : binding.incluirContent.getTrabalho().getMidias()) {

                if(midia.getContentType().contains("img")) {
                    binding.incluirContent.btnRemoverImagem.setVisibility(View.VISIBLE);
                    binding.fabFoto.setVisibility(View.GONE);
                }

                if(midia.getContentType().contains("vid")) {
                    binding.incluirContent.btnRemoverVideo.setVisibility(View.VISIBLE);
                    binding.fabVideo.setVisibility(View.GONE);
                }
            }

            for (Musica musica : binding.incluirContent.getTrabalho().getMusicas()){
                try {
                    listaMusicas = new ArrayList<>();
                    listaMusicas.add(musica);
                    binding.incluirContent.listaMusicas.setVisibility(View.VISIBLE);
                    if (binding.incluirContent.listaMusicas.getAdapter() != null && binding.incluirContent.listaMusicas.getAdapter() instanceof AdapterMusica){
                        ((AdapterMusica) binding.incluirContent.listaMusicas.getAdapter()).stopMedia();
                    }
                    binding.incluirContent.listaMusicas.setAdapter( new AdapterMusica(listaMusicas, this, "criando".equals(telaMode), TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario())));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    InputStream fis = getContentResolver().openInputStream(musica.getUri());

                    byte[] buf = new byte[1024];
                    int n;
                    while (-1 != (n = fis.read(buf)))
                        baos.write(buf, 0, n);

                    filesToUpload.add( new FileReference()
                            .setContentType("mus/" + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(musica.getUri())))
                            .setConteudo(baos.toByteArray())
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            preencherTela(binding.incluirContent.getTrabalho());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(FindFM.getMap().containsKey("USUARIO_BUSCA")){
            Musico musico = (Musico) FindFM.getMap().get("USUARIO_BUSCA");
            Set<Musico> musicos = new HashSet<>();
            musicos.addAll(((AdapterUsuario)binding.incluirContent.listaPessoas.getAdapter()).getUsuarios());
            List<String> ids = new ArrayList<>();
            for(Musico mus : musicos){
                ids.add(mus.getId()) ;
            }
            if(!ids.contains(musico.getId())){
                musicos.add(musico);
            }
            binding.incluirContent.getTrabalho().setMusicos( new ArrayList<>(musicos));
            binding.incluirContent.listaPessoas.setAdapter(new AdapterUsuario(musicos, this, false));
            FindFM.getMap().remove("USUARIO_BUSCA");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trabalho, menu);
        this.optionsMenu = menu;
        checkTelaMode();
        return true;
    }

    private void getTrabalho() {
        JsonTypedRequest<Usuario, ResponseBody, ErrorResponse> getTrabalho = new JsonTypedRequest<>
                (this,
                        Request.Method.GET,
                        Usuario.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        HttpUtils.buildUrl(getResources(), "work", binding.incluirContent.getTrabalho().getId()),
                        null,
                        (ResponseBody response) ->
                        {
                            dialog.hide();
                            if (ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                Trabalho trabalhoResponse = JsonUtils.jsonConvert(response.getData(), Trabalho.class);
                                binding.incluirContent.setTrabalho(trabalhoResponse);
                                binding.incluirContent.executePendingBindings();
                                checkTelaMode();
                                preencherTela(binding.incluirContent.getTrabalho());
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            dialog.hide();
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Response]WorkGet", errorResponse.getMessage());
                                mensagem = errorResponse.getMessage();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        },
                        (VolleyError errorResponse) ->
                        {
                            dialog.hide();
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Volley]WorkGet", errorResponse.getMessage());
                                errorResponse.printStackTrace();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        }
                );

        getTrabalho.execute();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.hideSoftKeyboard(this);
        switch (item.getItemId()) {
            case R.id.action_salvar:
                if (binding.incluirContent.txtDesc.getText().toString().isEmpty() || binding.incluirContent.txtTitulo.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Preencha os campos", Toast.LENGTH_SHORT).show();
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
                    if(listaMusicas.isEmpty()) {
                        initRequestTrabalho();
                    } else {
                        for(Musica musica : listaMusicas){
                            createMusica(musica);
                        }
                    }
                }
                break;
            case R.id.action_refresh:
                getTrabalho();
                break;
            case R.id.action_report:
                denunciar("Trabalho", binding.incluirContent.getTrabalho().getId());
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
                binding.incluirContent.btnRemoverImagem.setVisibility(View.VISIBLE);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                FileReference fileReference =  new FileReference()
                        .setContentType("img/jpeg")
                        .setConteudo(stream.toByteArray());
                binding.incluirContent.getTrabalho().getMidias().add(fileReference);
                filesToUpload.add(fileReference);
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
            Musica musica = new Musica()
                    .setDuracao(duracaoSegundos)
                    .setUri(uri);

            if(duracaoSegundos > 15L){
                AlertDialogUtils.newSimpleDialog__TwoButtons(this, "Atenção!", R.drawable.ic_error, "Este arquivo de áudio tem duração maior do que 15 segundos!\n" +
                                "Só permitimos postar arquivos de áudio maiores do que 15 segundos se as músicas nos mesmos forem de sua autoria.\nAs músicas deste arquivo de áudio são de sua autoria?",
                        "Sim, as músicas são de minha autoria.", "Não, as músicas não são de minha autoria.",
                        (dialogInterface, i) -> {
                            musica.setAutoral(true);
                            setAudio(musica);
                        },
                        (dialogInterface, i) -> { }).show();
            } else {
                AlertDialogUtils.newSimpleDialog__TwoButtons(this, "Atenção!", R.drawable.ic_error, "As músicas deste arquivo de áudio são de sua autoria?",
                        "Sim, as músicas são de minha autoria.", "Não, as músicas não são de minha autoria.",
                        (dialogInterface, i) -> {
                            musica.setAutoral(true);
                            setAudio(musica);
                        },
                        (dialogInterface, i) -> {
                            musica.setAutoral(false);
                            setAudio(musica);
                        }).show();
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
            binding.incluirContent.btnRemoverVideo.setVisibility(View.VISIBLE);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream fis = getContentResolver().openInputStream(u);

            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);

            FileReference fileReference = new FileReference()
                    .setContentType("video/" + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(u)))
                    .setConteudo(baos.toByteArray());
            filesToUpload.add( fileReference );
            binding.incluirContent.getTrabalho().getMidias().add(fileReference);
            checkTelaMode();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    private void setAudio(Musica musica){

        EditText input = new EditText(this);
        final String[] titulo = {""};
        AlertDialogUtils.newTextDialog(this, "Nova música", R.drawable.ic_audio, "Qual o nome da música?\nExemplo:\"Música A - Grupo X\"",
                "Adicionar", "Cancelar",
                (dialog, which) -> {
                    try {
                        titulo[0] = input.getText().toString();
                        musica.setNome(titulo[0]);

                        boolean jaContem = false;
                        for(int i = 0; i < listaMusicas.size(); i++) {
                            if(listaMusicas.get(i).equals(musica)) {
                                jaContem = true;
                                AlertDialogUtils.newSimpleDialog__OneButton(this,
                                        "Música já adicionada!", R.drawable.ic_error,
                                        "Essa música já foi adicionada, adicione uma música diferente!",
                                        "Ok", (dialogInterface, i1) -> {}).show();
                            }
                        }

                        if(!jaContem) {
                            listaMusicas.add(musica);
                        }

                        binding.incluirContent.listaMusicas.setVisibility(View.VISIBLE);
                        if (binding.incluirContent.listaMusicas.getAdapter() != null && binding.incluirContent.listaMusicas.getAdapter() instanceof AdapterMusica){
                            ((AdapterMusica) binding.incluirContent.listaMusicas.getAdapter()).stopMedia();
                        }
                        binding.incluirContent.listaMusicas.setAdapter( new AdapterMusica(listaMusicas, this, "criando".equals(telaMode), TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario())));
                        checkTelaMode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                (dialog, which) -> { }, input).show();

    }

    private void denunciar(String tipo, String idItem){
        EditText input = new EditText(this);
        AlertDialogUtils.newTextDialog(this, "Denunciar " + tipo + " ?", R.drawable.ic_report, "Diga-nos o que está errado e tomaremos as devidas providências!",
                "Denunciar", "Cancelar",
                (dialog, which) -> {
                    try {
                        JsonTypedRequest<Denuncia, ResponseBody, ErrorResponse> reportRequest = new JsonTypedRequest<>(
                                this,
                                HttpMethod.POST.getCodigo(),
                                Denuncia.class,
                                ResponseBody.class,
                                ErrorResponse.class,
                                HttpUtils.buildUrl(getResources(),"report"),
                                null,
                                (ResponseBody response) -> {
                                    this.dialog.hide();
                                    if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                        AlertDialogUtils.newSimpleDialog__OneButton(this,
                                                "Sucesso!", R.drawable.ic_error,
                                                "Denúncia enviada com sucesso!","OK",
                                                (dialog1, id) -> this.dialog.setMessage("Carregando...")).create().show();
                                    }
                                },
                                (ErrorResponse errorResponse) ->
                                {
                                    this.dialog.hide();
                                    String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                                    if(errorResponse != null) {
                                        Log.e("[ERRO-Response]Denuncia", errorResponse.getMessage());
                                        mensagem = errorResponse.getMessage();
                                    }
                                    AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                            mensagem, "OK", (dialog2, id) -> { }).create().show();
                                },
                                (VolleyError errorResponse) ->
                                {
                                    this.dialog.hide();
                                    String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                                    if(errorResponse != null) {
                                        Log.e("[ERRO-Volley]Denuncia", errorResponse.getMessage());
                                        errorResponse.printStackTrace();
                                    }
                                    AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                            mensagem, "OK", (dialog2, id) -> { }).create().show();
                                }
                        );
                        reportRequest.setRequest(new Denuncia()
                                .setId(idItem)
                                .setContato(FindFM.getUsuario().getId())
                                .setMotivo(input.getText().toString())
                                .setTipo(tipo)
                        );
                        this.dialog.setMessage("Enviando denúncia, aguarde...");
                        this.dialog.show();
                        reportRequest.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                (dialog, which) -> { }, input).show();
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
        binding.incluirContent.getTrabalho().getMidias().removeAll(filesToRemove);
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
        binding.incluirContent.getTrabalho().getMidias().removeAll(filesToRemove);
        checkTelaMode();
    }

    private void initRequestTrabalho() {
        JsonTypedRequest<Trabalho, ResponseBody, ErrorResponse> trabalhoRequest = new JsonTypedRequest<>(
                this,
                HttpMethod.POST.getCodigo(),
                Trabalho.class,
                ResponseBody.class,
                ErrorResponse.class,
                HttpUtils.buildUrl(getResources(), "work", "create"),
                null,
                (ResponseBody response) -> {
                    this.dialog.hide();
                    if (ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                        AlertDialogUtils.newSimpleDialog__OneButton(this,
                                "Sucesso!", R.drawable.ic_save,
                                "Trabalho cadastrado com sucesso!", "OK",
                                (dialog, id) -> {
                                    dialog.dismiss();
                                    FindFM.setTelaAtual("TRABALHO_CRIADO");
                                    binding.incluirContent.getTrabalho().setId(response.getData().toString());
                                    telaMode = "visualizar";
                                    getTrabalho();
                                }).create().show();
                    }
                },
                (ErrorResponse errorResponse) ->
                {
                    dialog.hide();
                    String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                    if(errorResponse != null) {
                        Log.e("[ERRO-Response]WorkAdd", errorResponse.getMessage());
                        mensagem = errorResponse.getMessage();
                    }
                    AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                            mensagem, "OK", (dialog, id) -> { }).create().show();
                },
                (VolleyError errorResponse) ->
                {
                    dialog.hide();
                    String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                    if(errorResponse != null) {
                        Log.e("[ERRO-Volley]WorkAdd", errorResponse.getMessage());
                        errorResponse.printStackTrace();
                    }
                    AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                            mensagem, "OK", (dialog, id) -> { }).create().show();
                }
        );

        trabalhoRequest.setRequest(binding.incluirContent.getTrabalho());
        dialog.setMessage("Publicando, aguarde...");
        dialog.show();
        trabalhoRequest.execute();
    }

    private void createMusica(Musica musica){
        UploadResourceService uploadMusica = new UploadResourceService(this);
        uploadMusica.addObserver((o, arg) -> {
            if(o instanceof UploadResourceService){
                runOnUiThread(() -> {
                    if (arg instanceof String) {
                        String result = (String) arg;
                        String[] resultados = result.split(",");
                        if(resultados.length == 1){
                            return;
                        }
                        musica.setIdResource(resultados[1]);
                        JsonTypedRequest<Musica, ResponseBody, ErrorResponse> musicaRequest = new JsonTypedRequest<>(
                                this, HttpMethod.POST.getCodigo(), Musica.class, ResponseBody.class, ErrorResponse.class,
                                HttpUtils.buildUrl(getResources(), "song", "create"), null,
                                (ResponseBody response) -> {
                                    this.dialog.hide();
                                    if (ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                        Trabalho trabalho = binding.incluirContent.getTrabalho();
                                        musica.setIdResource(response.getData().toString());
                                        trabalho.getMusicas().add(musica);

                                        binding.incluirContent.setTrabalho(trabalho);
                                        musicasEnviadas.add(musica.getNome());

                                        for (Musica mus: listaMusicas){
                                            if(!musicasEnviadas.contains(mus.getNome())){
                                                return;
                                            }
                                        }

                                        initRequestTrabalho();

                                    }
                                },
                                (ErrorResponse errorResponse) ->
                                {
                                    dialog.hide();
                                    String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                                    if(errorResponse != null) {
                                        Log.e("[ERRO-Response]SongAdd", errorResponse.getMessage());
                                        mensagem = errorResponse.getMessage();
                                    }
                                    AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                            mensagem, "OK", (dialog, id) -> { }).create().show();
                                },
                                (VolleyError errorResponse) ->
                                {
                                    dialog.hide();
                                    String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                                    if(errorResponse != null) {
                                        Log.e("[ERRO-Volley]SongAdd", errorResponse.getMessage());
                                        errorResponse.printStackTrace();
                                    }
                                    AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                            mensagem, "OK", (dialog, id) -> { }).create().show();
                                }
                        );

                        musicaRequest.setRequest(musica);
                        dialog.setMessage("Publicando música, aguarde...");
                        dialog.show();
                        musicaRequest.execute();
                    }
                });
            }
        });
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream fis = getContentResolver().openInputStream(musica.getUri());

            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
            uploadMusica.uploadFiles(baos.toByteArray(), "mus/" + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(musica.getUri())));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    public void setTrabalho(Trabalho param) {
        binding.incluirContent.setTrabalho(param);
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
                                    "\nVerifique sua conexão com a Internet e tente novamente.","OK",
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
                                        "\nVerifique sua conexão com a Internet e tente novamente.","OK",
                                (dialog, id) -> { }).create().show();
                        return;
                    }

                    binding.incluirContent.getTrabalho()
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
                        if(listaMusicas.isEmpty()) {
                            initRequestTrabalho();
                        } else {
                            for(Musica musica : listaMusicas){
                                createMusica(musica);
                            }
                        }
                    }
                }
            });
        }
    }
}
