package com.fatec.tcc.findfm.Views.Adapters;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatec.tcc.findfm.Infrastructure.Request.DownloadResourceService;
import com.fatec.tcc.findfm.Model.Business.FileReference;
import com.fatec.tcc.findfm.Model.Business.Trabalho;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Views.TelaPrincipal;
import com.fatec.tcc.findfm.databinding.ViewTrabalhoBinding;
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
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class AdapterTrabalhos extends RecyclerView.Adapter<AdapterTrabalhos.ViewHolder> {

    private List<Trabalho> trabalhos = new ArrayList<>();
    private TelaPrincipal activity;
    private boolean isAutor;

    public AdapterTrabalhos() {
    }

    public AdapterTrabalhos(List<Trabalho> trabalhos, TelaPrincipal activity, boolean isAutor){
        this.trabalhos = trabalhos;
        this.activity = activity;
        this.isAutor = isAutor;
    }

    public List<Trabalho> getTrabalhos(){
        return new ArrayList<>(this.trabalhos);
    }

    @Override
    public AdapterTrabalhos.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewTrabalhoBinding binding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.view_trabalho, parent, false);

        return new AdapterTrabalhos.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(AdapterTrabalhos.ViewHolder holder, int position) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Carregando...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        Trabalho trabalho = trabalhos.get(position);
        holder.bindingVH.setTrabalho(trabalho);
        holder.bindingVH.fotoPublicacao.setVisibility(View.GONE);
        holder.bindingVH.videoView.setVisibility(View.GONE);

        for(FileReference midia : trabalho.getMidias()) {

            if(midia.getContentType().contains("img")) {
                DownloadResourceService downloadService = new DownloadResourceService(activity);
                downloadService.addObserver((download, arg) -> {
                    if (download instanceof DownloadResourceService) {
                        activity.runOnUiThread(() -> {
                            if (arg instanceof BinaryResponse) {
                                byte[] dados = ((BinaryResponse) arg).getData();
                                InputStream input = new ByteArrayInputStream(dados);
                                Bitmap ext_pic = BitmapFactory.decodeStream(input);
                                holder.bindingVH.fotoPublicacao.setImageBitmap(ext_pic);
                                holder.bindingVH.fotoPublicacao.setVisibility(View.VISIBLE);
                            } else {
                                Log.e("[ERRO-Download]IMG", "Erro ao baixar binário da imagem");
                                AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                        "Ops!", R.drawable.ic_error,
                                        "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                                "\nVerifique sua conexão com a Internet e tente novamente.", "OK",
                                        (dialog, id1) -> {
                                        }).create().show();
                            }
                            progressDialog.hide();
                        });
                    }
                });
                downloadService.getResource(midia.getId());
                progressDialog.show();
            }

            if(midia.getContentType().contains("vid")) {
                try {
                    Uri uri = Uri.parse(HttpUtils.buildUrl(activity.getResources(), "resource/" + midia.getId()));

                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                    SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);
                    DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                    MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
                    holder.bindingVH.videoView.setPlayer(exoPlayer);
                    exoPlayer.prepare(mediaSource);
                    exoPlayer.seekTo(100);
                    holder.bindingVH.videoView.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        holder.bindingVH.viewMusicas.setLayoutManager(new LinearLayoutManager(activity));
        holder.bindingVH.viewMusicas.setVisibility(View.VISIBLE);
        //TODO: na lista, não mostrar o checkbox editavel
        holder.bindingVH.viewMusicas.setAdapter(new AdapterMusica(trabalho.getMusicas(), activity, false, isAutor));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(activity.getDrawable(R.drawable.divider)));
        holder.bindingVH.viewMusicas.addItemDecoration(itemDecorator);

        holder.bindingVH.viewFeats.setLayoutManager(new LinearLayoutManager(activity));
        holder.bindingVH.viewFeats.setVisibility(View.VISIBLE);
        //TODO: na lista, clicar leva pro perfil
        holder.bindingVH.viewFeats.setAdapter(new AdapterUsuario(new HashSet<>(trabalho.getMusicos()), activity, false, true));
        holder.bindingVH.viewMusicas.addItemDecoration(itemDecorator);
        /*
        holder.bindingVH.setClickListener(v -> {
            if(trabalho.getAutor().getId().equals(FindFM.getUsuario().getId())){
                String path = "CriarTrabalho";
                Bundle param = new Bundle();
                FindFM.getMap().put("trabalho", trabalho);
                param.putString("telaMode", "editavel");
                param.putBoolean("visitante", TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario()));
                Util.open_form_withParam(activity, CriarTrabalho.class, path, param);
            } else {
                String path = "CriarTrabalho";
                Bundle param = new Bundle();
                FindFM.getMap().put("trabalho", trabalho);
                param.putString("telaMode", "visualizar");
                param.putBoolean("visitante", TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario()));
                Util.open_form_withParam(activity, CriarTrabalho.class, path, param);
            }
        });
        */
        progressDialog.hide();
    }

    @Override
    public int getItemCount() {
        if (trabalhos == null) {
            return 0;
        }
        return trabalhos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewTrabalhoBinding bindingVH;

        ViewHolder(ViewTrabalhoBinding binding){
            super(binding.getRoot());
            this.bindingVH = binding;
        }
    }
}