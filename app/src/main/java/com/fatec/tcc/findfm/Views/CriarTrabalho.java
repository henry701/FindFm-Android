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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.DownloadResourceService;
import com.fatec.tcc.findfm.Infrastructure.Request.UploadResourceService;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Trabalho;
import com.fatec.tcc.findfm.Model.Business.Usuario;
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
import java.util.Collections;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class CriarTrabalho extends AppCompatActivity implements Observer {

    private ActivityCriarTrabalhoBinding binding;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int PICK_AUDIO = 3;
    private ProgressDialog dialog;

    private String telaMode = "criando";

    private boolean fotoUpload;
    private byte[] fotoBytes;
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

    //TODO: podem ter varios audios!!
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FindFM.setTelaAtual("CRIAR_POST");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_criar_trabalho);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle param = getIntent().getBundleExtra("CriarTrabalho");
        if (param != null) {
            if (!param.isEmpty()) {
                telaMode = param.getString("telaMode");
            }
        } else {
            binding.incluirContent.setTrabalho(new Trabalho());
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

        if (telaMode.equals("criando")) {
            binding.incluirContent.setTrabalho(new Trabalho().setMusicos(Collections.singletonList(FindFM.getMusico())));
        } else if (telaMode.equals("visualizar") || telaMode.equals("editavel")) {
            Trabalho trabalho = (Trabalho) FindFM.getMap().get("trabalho");
            binding.incluirContent.setTrabalho(trabalho);
            getTrabalho();
        }

    }

    private void preencherTela(Trabalho trabalho) {

        for (String id : trabalho.getIdFotos()) {

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
                            this.fotoBytes_ContentType = "image/jpeg";
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
            downloadService.getResource(id);
            dialog.show();
        }

        if (trabalho.getMusicos() == null) {
            binding.incluirContent.lbParticipantes.setText(R.string.sem_integrantes);
            binding.incluirContent.listaPessoas.setVisibility(View.GONE);
        } else {
            //TODO: adapter de usuarios
        }

        for (String id : trabalho.getIdVideos()) {
            try {
                Uri uri = Uri.parse(HttpUtils.buildUrl(getResources(), "resource/" + id));

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


        for (String idAudio : trabalho.getIdAudios()) {
            try {
                Uri uri = Uri.parse(HttpUtils.buildUrl(getResources(), "resource/" + idAudio));
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

    private void checkTelaMode() {
        if (telaMode.equals("visualizar")) {
            binding.incluirContent.txtTitulo.setEnabled(false);
            binding.incluirContent.txtDesc.setEnabled(false);

            binding.fabFoto.setVisibility(View.INVISIBLE);
            binding.fabVideo.setVisibility(View.INVISIBLE);
            binding.fabAudio.setVisibility(View.INVISIBLE);
            binding.fabPessoa.setVisibility(View.INVISIBLE);

            binding.incluirContent.btnRemoverImagem.setVisibility(View.GONE);
            binding.incluirContent.btnRemoverVideo.setVisibility(View.GONE);
            binding.incluirContent.btnRemoverAudio.setVisibility(View.GONE);

            optionsMenu.getItem(0).setVisible(false);
            optionsMenu.getItem(1).setVisible(true);

        } else if (telaMode.equals("criando")) {
            if (optionsMenu != null) {
                optionsMenu.getItem(0).setVisible(true);
                optionsMenu.getItem(1).setVisible(false);
            }
            binding.incluirContent.txtTitulo.setEnabled(true);
            binding.incluirContent.txtDesc.setEnabled(true);

            if (fotoBytes == null) {
                binding.incluirContent.btnRemoverImagem.setVisibility(View.GONE);
                binding.fabFoto.setVisibility(View.VISIBLE);
            } else {
                binding.incluirContent.btnRemoverImagem.setVisibility(View.VISIBLE);
                binding.fabFoto.setVisibility(View.GONE);
            }

            if (videoBytes == null) {
                binding.incluirContent.btnRemoverVideo.setVisibility(View.GONE);
                binding.fabVideo.setVisibility(View.VISIBLE);
            } else {
                binding.incluirContent.btnRemoverVideo.setVisibility(View.VISIBLE);
                binding.fabVideo.setVisibility(View.GONE);
            }

            if (audioBytes == null) {
                binding.incluirContent.btnRemoverAudio.setVisibility(View.GONE);
                binding.fabAudio.setVisibility(View.VISIBLE);
            } else {
                binding.incluirContent.btnRemoverAudio.setVisibility(View.VISIBLE);
                binding.fabAudio.setVisibility(View.GONE);
            }
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
                        HttpUtils.buildUrl(getResources(), "trabalho", binding.incluirContent.getTrabalho().getId()),
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
                            AlertDialogUtils.newSimpleDialog__OneButton(this,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(), "OK",
                                    (dialog, id) -> {
                                    }).create().show();
                        },
                        (VolleyError error) ->
                        {
                            dialog.hide();
                            error.printStackTrace();
                            AlertDialogUtils.newSimpleDialog__OneButton(this,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente", "OK",
                                    (dialog, id) -> {
                                    }).create().show();
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
                if (fotoBytes != null) {
                    fotoUpload = true;
                    resourceService.uploadFiles(fotoBytes, fotoBytes_ContentType, "foto");
                }
                if (videoBytes != null) {
                    videoUpload = true;
                    resourceService.uploadFiles(videoBytes, videoBytes_ContentType, "video");
                }
                if (audioBytes != null) {
                    audioUpload = true;
                    resourceService.uploadFiles(audioBytes, audioBytes_ContentType, "audio");
                }

                if (!fotoUpload && !videoUpload && !audioUpload) {
                    initRequest(binding.incluirContent.getTrabalho());
                }
                break;
            case R.id.action_refresh:
                getTrabalho();
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
                fotoBytes = stream.toByteArray();
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

            if (duracaoSegundos > 15L) {
                AlertDialogUtils.newSimpleDialog__TwoButtons(this, "Atenção!", R.drawable.ic_error, "Este vídeo tem duração maior do que 15 segundos!\n" +
                                "Só permitimos trabalhoar vídeos maiores do que 15 segundos se as músicas nos mesmos forem de sua autoria.\nAs músicas deste vídeo são de sua autoria?",
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

            if (duracaoSegundos > 15L) {
                AlertDialogUtils.newSimpleDialog__TwoButtons(this, "Atenção!", R.drawable.ic_error, "Este arquivo de áudio tem duração maior do que 15 segundos!\n" +
                                "Só permitimos trabalhoar arquivos de áudio maiores do que 15 segundos se as músicas nos mesmos forem de sua autoria.\nAs músicas deste arquivo de áudio são de sua autoria?",
                        "Sim, as músicas são de minha autoria.", "Não, as músicas não são de minha autoria.",
                        (dialogInterface, i) -> setAudio(uri),
                        (dialogInterface, i) -> {
                        }).show();
            } else {
                setAudio(uri);
            }

        }
    }

    private void setVideo(Uri u) {
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

            this.videoBytes = baos.toByteArray();
            this.videoBytes_ContentType = "video/" + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(u));
            checkTelaMode();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setAudio(Uri uri) {
        try {
            //TODO: PODEM TER VARIOS
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

    public void btnRemoverImagem_Click(View v) {
        fotoUpload = false;
        fotoBytesId = null;
        fotoBytes = null;
        fotoBytes_ContentType = null;
        binding.incluirContent.fotoPublicacao.setVisibility(View.GONE);
        checkTelaMode();
    }

    public void btnRemoverVideo_Click(View v) {
        videoUpload = false;
        videoBytesId = null;
        videoBytes = null;
        videoBytes_ContentType = null;
        binding.incluirContent.videoView.setVisibility(View.GONE);
        checkTelaMode();
    }

    public void btnRemoverAudio_Click(View v) {
        //TODO: REMOVER VARIOS?
        audioUpload = false;
        audioBytesId = null;
        audioBytes = null;
        audioBytes_ContentType = null;
        getFragmentManager().findFragmentById(R.id.frame_audio).onDestroy();
        binding.incluirContent.frameAudio.setVisibility(View.GONE);
        binding.incluirContent.btnRemoverAudio.setVisibility(View.GONE);
        binding.fabAudio.setVisibility(View.VISIBLE);
    }

    private void initRequest(Trabalho trabalho) {
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
                                    binding.incluirContent.setTrabalho(new Trabalho().setId(response.getData().toString()));
                                    telaMode = "visualizar";
                                    getTrabalho();
                                }).create().show();

                    }
                },
                (ErrorResponse error) -> {
                    dialog.hide();
                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Ops!", R.drawable.ic_error,
                            error.getMessage(), "OK",
                            (dialog, id) -> {
                            }).create().show();
                },
                (VolleyError error) -> {
                    dialog.hide();
                    error.printStackTrace();
                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Ops!", R.drawable.ic_error,
                            "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                    "\nVerifique sua conexão com a Internet e tente novamente", "OK",
                            (dialog, id) -> {
                            }).create().show();
                }
        );

        trabalhoRequest.setRequest(binding.incluirContent.getTrabalho());
        dialog.setMessage("Publicando, aguarde...");
        dialog.show();
        trabalhoRequest.execute();
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
        //TODO: fazer tentar novamente
        if (upload instanceof UploadResourceService) {
            runOnUiThread(() -> {
                if (arg instanceof ErrorResponse) {
                    ErrorResponse error = (ErrorResponse) arg;
                    dialog.hide();

                    String result = (String) arg;
                    if (result.equals("foto"))
                        fotoUpload = false;
                    else if (result.equals("video"))
                        videoUpload = false;
                    else
                        audioUpload = false;
                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Ops!", R.drawable.ic_error,
                            error.getMessage(), "OK",
                            (dialog, id) -> {
                            }).create().show();
                } else if (arg instanceof Exception) {
                    Exception error = (Exception) arg;
                    dialog.hide();

                    String result = (String) arg;
                    if (result.equals("foto"))
                        fotoUpload = false;
                    else if (result.equals("video"))
                        videoUpload = false;
                    else
                        audioUpload = false;

                    error.printStackTrace();
                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Ops!", R.drawable.ic_error,
                            "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                    "\nVerifique sua conexão com a Internet e tente novamente", "OK",
                            (dialog, id) -> {
                            }).create().show();
                } else if (arg instanceof String) {
                    String result = (String) arg;
                    String[] resultados = result.split(",");

                    //Por algum motivo as vezes não retorna o id, isso é só pra nao dar exception

                    if (resultados.length == 1) {
                        dialog.hide();
                        AlertDialogUtils.newSimpleDialog__OneButton(this,
                                "Ops!", R.drawable.ic_error,
                                "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                        "\nVerifique sua conexão com a Internet e tente novamente", "OK",
                                (dialog, id) -> {
                                }).create().show();
                        return;
                    }

                    if (resultados[0].equals("foto")) {
                        fotoUpload = false;
                        fotoBytesId = resultados[1];
                        binding.incluirContent.getTrabalho().setIdFotos(Collections.singletonList(fotoBytesId));
                    } else if (resultados[0].equals("video")) {
                        videoUpload = false;
                        videoBytesId = resultados[1];
                        binding.incluirContent.getTrabalho().setIdVideos(Collections.singletonList(videoBytesId));
                    } else if (resultados[0].equals("audio")) {
                        audioUpload = false;
                        audioBytesId = resultados[1];
                        binding.incluirContent.getTrabalho().setIdAudios(Collections.singletonList(audioBytesId));
                    }

                    if (!videoUpload && !fotoUpload && !audioUpload) {
                        initRequest(binding.incluirContent.getTrabalho());
                    }
                }
            });
        }
    }
}
