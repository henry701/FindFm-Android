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
import android.text.InputFilter;
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
import java.util.Map;
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
    private String idAutor;
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
            dialog = new ProgressDialog(this);
            dialog.setMessage("Carregando...");
            dialog.setCancelable(false);
            dialog.setInverseBackgroundForced(false);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            Bundle param = getIntent().getBundleExtra("CriarTrabalho");
            if (param != null) {
                if (!param.isEmpty()) {
                    telaMode = param.getString("telaMode");
                    idAutor = param.getString("idAutor");
                }
            }

            if (telaMode.equals("criando")) {
                List<Musico> musicos = new ArrayList<>();
                musicos.add(FindFM.getMusico());
                idAutor = FindFM.getUsuario().getId();
                binding.incluirContent.setTrabalho(
                        new Trabalho()
                                .setMusicos(musicos)
                                .setMidias(new ArrayList<>())
                                .setMusicas(new ArrayList<>()));
                getSupportActionBar().setTitle("Novo Trabalho");
            }
            else {
                Trabalho trabalho = (Trabalho) FindFM.getMap().get("trabalho");
                binding.incluirContent.setTrabalho(trabalho);
                checkTelaMode();
                preencherTela(trabalho);
                getSupportActionBar().setTitle(trabalho.getNome());
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

        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(getDrawable(R.drawable.divider)));
        binding.incluirContent.listaMusicas.setLayoutManager(new LinearLayoutManager(this));
        binding.incluirContent.listaMusicas.addItemDecoration(itemDecorator);
        binding.incluirContent.btnAdicionarMusica.setOnClickListener(v -> startActivityForResult(Intent.createChooser(MidiaUtils.pickAudioIntent(), "Escolha a música"), PICK_AUDIO));
        binding.incluirContent.btnAdicionarPessoa.setOnClickListener(v -> Util.open_form(getApplicationContext(), SearchUsuario.class));
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
                    binding.incluirContent.videoView.setOnLongClickListener(v -> {
                        denunciar("Vídeo", midia.getId());
                        return true;
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(trabalho.getMusicas() != null){
            listaMusicas = new ArrayList<>();

            for(Musica musica : trabalho.getMusicas()) {
                boolean jaContem = false;
                for (int i = 0; i < listaMusicas.size(); i++) {
                    if (listaMusicas.get(i).equals(musica)) {
                        jaContem = true;
                    }
                }

                if (!jaContem) {
                    listaMusicas.add(musica);
                }
            }
                binding.incluirContent.listaMusicas.setVisibility(View.VISIBLE);
                if (binding.incluirContent.listaMusicas.getAdapter() != null && binding.incluirContent.listaMusicas.getAdapter() instanceof AdapterMusica){
                    ((AdapterMusica) binding.incluirContent.listaMusicas.getAdapter()).stopMedia();
                }

            binding.incluirContent.listaMusicas.setAdapter( new AdapterMusica(listaMusicas, this, "criando".equals(telaMode), "editavel".equals(telaMode), idAutor));

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
            binding.incluirContent.listaPessoas.setAdapter(new AdapterUsuario(new HashSet<>(trabalho.getMusicos()), this, false, true));
        }

    }

    private void checkTelaMode() {
        if (telaMode.equals("visualizar") || telaMode.equals("editavel")) {
            binding.incluirContent.txtTitulo.setEnabled(false);
            binding.incluirContent.txtDesc.setEnabled(false);
            binding.fabFoto.setVisibility(View.INVISIBLE);
            binding.fabVideo.setVisibility(View.INVISIBLE);
            binding.incluirContent.checkOriginal.setEnabled(false);
            binding.incluirContent.btnRemoverImagem.setVisibility(View.GONE);
            binding.incluirContent.btnRemoverVideo.setVisibility(View.GONE);
            binding.incluirContent.btnAdicionarMusica.setVisibility(View.GONE);
            binding.incluirContent.btnAdicionarPessoa.setVisibility(View.GONE);

            try {
                optionsMenu.getItem(0).setVisible(false);
                optionsMenu.getItem(1).setVisible(false);
                optionsMenu.getItem(2).setVisible(!(telaMode.equals("editavel")));
            } catch (Exception e){
                e.printStackTrace();
            }

            try {
                getSupportActionBar().setTitle("Trabalho");
            } catch (Exception e){
                e.printStackTrace();
            }

            preencherTela(binding.incluirContent.getTrabalho());

        } else if (telaMode.equals("criando")) {
            try {
                getSupportActionBar().setTitle("Novo Trabalho");
            } catch (Exception e){
                e.printStackTrace();
            }
            try{
                if (optionsMenu != null) {
                    optionsMenu.getItem(0).setVisible(true);
                    optionsMenu.getItem(1).setVisible(false);
                    optionsMenu.getItem(2).setVisible(false);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
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
                    binding.incluirContent.listaMusicas.setAdapter( new AdapterMusica(listaMusicas, this, "criando".equals(telaMode), TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario()), ""));
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

                    try {
                        listaMusicas = ((AdapterMusica)binding.incluirContent.listaMusicas.getAdapter()).getMusicas();

                        for(Musica musica : listaMusicas){
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
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    if(listaMusicas.isEmpty()) {
                        criarTrabalhoRequest();
                    } else {
                        for(Musica musica : listaMusicas){
                            createMusica(musica);
                        }
                    }
                }
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
        AlertDialogUtils.newTextDialog(this, "Nova música", R.drawable.ic_audio, "Qual o nome da música?\nExemplo:\"Música A - Seu Nome\"\nObs.: Coloque seu nome pra que possam te encontrar",
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
                        binding.incluirContent.listaMusicas.setAdapter( new AdapterMusica(listaMusicas, this, "criando".equals(telaMode), TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario()), ""));
                        binding.incluirContent.getTrabalho().setMusicas(listaMusicas);
                        checkTelaMode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                (dialog, which) -> { }, input).show();

    }

    private void denunciar(String tipo, String idItem){
        EditText motivo = new EditText(this);
        EditText contato = new EditText(this);
        InputFilter[] filtros =new InputFilter[] {new InputFilter.LengthFilter(60)};
        motivo.setFilters(filtros);
        contato.setFilters(filtros);
        AlertDialogUtils.newTextDialog(this, "Denunciar " + tipo + " ?", R.drawable.ic_report,
                "Diga-nos o que está errado e tomaremos as devidas providências!\nEscreva aqui sua denúncia:",
                "Denunciar", "Cancelar",
                (dialog, which) ->
                        AlertDialogUtils.newTextDialog(this, "Denunciar " + tipo + " ?", R.drawable.ic_report,
                                "Diga-nos como podemos te contatar para falar sobre essa denúncia.\nSeu nome e e-mail para contato:",
                                "Denunciar", "Cancelar",
                                (dialog4, which4) -> {
                                    if(motivo.getText() != null && !"".equals(motivo.getText().toString()) &&
                                            contato.getText() != null && !"".equals(contato.getText().toString()) ) {
                                        String denuncia = motivo.getText().toString();
                                        String denunciante = contato.getText().toString();
                                        initDenunciarRequest(idItem, denuncia, denunciante, tipo);
                                    } else {
                                        Toast.makeText(this, "Preencha todos os campos para enviar denúncia!", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                (dialog2, which2) -> { }, contato).show(),
                (dialog, which) -> { }, motivo).show();
    }

    private void initDenunciarRequest(String idItem, String motivo, String contato, String tipo){
        JsonTypedRequest<Denuncia, ResponseBody, ErrorResponse> reportRequest = new JsonTypedRequest<>(
                this,
                HttpMethod.POST.getCodigo(),
                Denuncia.class,
                ResponseBody.class,
                ErrorResponse.class,
                HttpUtils.buildUrl(getResources(),"report"),
                null,
                (ResponseBody response) -> Toast.makeText(this, "Denúncia enviada com sucesso!", Toast.LENGTH_SHORT).show(),
                (ErrorResponse errorResponse) ->
                {
                    if(errorResponse != null) {
                        Log.e("[ERRO-Response]Denuncia", errorResponse.getMessage());
                    }
                },
                (VolleyError errorResponse) ->
                {
                    if(errorResponse != null) {
                        Log.e("[ERRO-Response]Denuncia", errorResponse.getMessage());
                    }
                }
        );

        reportRequest.setRequest(new Denuncia()
                .setId(idItem)
                .setContato(contato)
                .setMotivo(motivo)
                .setTipo(tipo)
        );
        Toast.makeText(this, "Enviando denúncia...", Toast.LENGTH_SHORT).show();
        reportRequest.execute();
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

    private void criarTrabalhoRequest() {
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
                                    telaMode = "editavel";
                                    checkTelaMode();
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
                                        musica.setId(response.getData().toString());
                                        trabalho.getMusicas().add(musica);

                                        binding.incluirContent.setTrabalho(trabalho);
                                        musicasEnviadas.add(musica.getNome());

                                        for (Musica mus: listaMusicas){
                                            if(!musicasEnviadas.contains(mus.getNome())){
                                                return;
                                            }
                                        }

                                        criarTrabalhoRequest();

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
                        dialog.setMessage("Publicando músicas, aguarde...");
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

    private void getMusicas(String idMusica){
        JsonTypedRequest<Musica, ResponseBody, ErrorResponse> registrarRequest = new JsonTypedRequest<>
                (       this,
                        Request.Method.GET,
                        Musica.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        HttpUtils.buildUrl(getResources(), "song", idAutor, idMusica),
                        null,
                        (ResponseBody response) ->
                        {
                            dialog.hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                Musica musica = JsonUtils.jsonConvert(((Map<String, Object>) response.getData()), Musica.class);
                                listaMusicas.add(musica);
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            dialog.dismiss();
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Response]GetMusic", errorResponse.getMessage());
                                mensagem = errorResponse.getMessage();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        },
                        (VolleyError errorResponse) ->
                        {
                            dialog.dismiss();
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Volley]GetMusic", errorResponse.getMessage());
                                errorResponse.printStackTrace();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        }
                );
        registrarRequest.execute();
        dialog.show();
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
                            criarTrabalhoRequest();
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
