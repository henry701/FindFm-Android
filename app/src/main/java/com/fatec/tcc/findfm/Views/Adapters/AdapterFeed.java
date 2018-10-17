package com.fatec.tcc.findfm.Views.Adapters;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.DownloadResourceService;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Post;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Http.Request.ComentarRequest;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpMethod;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Audio_Fragment;
import com.fatec.tcc.findfm.Views.CriarPost;
import com.fatec.tcc.findfm.Views.TelaPrincipal;
import com.fatec.tcc.findfm.databinding.ViewItemFeedBinding;
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
import java.util.List;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.ViewHolder> {

    private List<Post> posts = new ArrayList<>();
    private TelaPrincipal activity;
    private boolean isVisitante;

    public AdapterFeed() {
    }

    public AdapterFeed(List<Post> posts, TelaPrincipal activity, boolean isVisitante){
        this.posts = posts;
        this.activity = activity;
        this.isVisitante = isVisitante;
    }

    public List<Post> getPosts(){
        return new ArrayList<>(this.posts);
    }

    @Override
    public AdapterFeed.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewItemFeedBinding binding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.view_item_feed, parent, false);

        return new AdapterFeed.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(AdapterFeed.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bindingVH.setPost(post);

        holder.bindingVH.fotoPublicacao.setVisibility(View.GONE);
        holder.bindingVH.videoView.setVisibility(View.GONE);

        for(String id : post.getIdFotos()){

            DownloadResourceService downloadService = new DownloadResourceService(activity);
            downloadService.addObserver( (download, arg) -> {
                if(download instanceof DownloadResourceService) {
                    activity.runOnUiThread(() -> {
                        if (arg instanceof BinaryResponse) {
                            byte[] dados = ((BinaryResponse) arg).getData();
                            InputStream input=new ByteArrayInputStream(dados);
                            Bitmap ext_pic = BitmapFactory.decodeStream(input);
                            post.setFotoBytes(dados);
                            holder.bindingVH.fotoPublicacao.setImageBitmap(ext_pic);
                            holder.bindingVH.fotoPublicacao.setVisibility(View.VISIBLE);
                        } else{
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id1) -> { }).create().show();
                        }

                        activity.getDialog().hide();

                    });
                }
            });
            downloadService.getResource(id);
            activity.getDialog().show();
        }

        for(String id : post.getIdVideos()){
            try {
                Uri uri = Uri.parse(HttpUtils.buildUrl(activity.getResources(), "resource/" + id));

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
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if(post.getIdAudio() != null) {
            try {
                Uri uri = Uri.parse(HttpUtils.buildUrl(activity.getResources(), "resource/" + post.getIdAudio()));
                holder.bindingVH.frameAudio.setVisibility(View.VISIBLE);
                activity.getFragmentManager().beginTransaction().replace(R.id.frame_audio,
                        new Audio_Fragment(activity, uri))
                        .commit();
            } catch (Exception e){
                e.printStackTrace();
                AlertDialogUtils.newSimpleDialog__OneButton(activity,
                        "Ops!", R.drawable.ic_error,
                        "Não foi possível obter a música." +
                                "\nVerifique sua conexão com a Internet e tente novamente","OK",
                        (dialog, id1) -> { }).create().show();
            }
        }

        if(post.getAutor().getFotoID() != null){

            DownloadResourceService downloadService = new DownloadResourceService(activity);
            downloadService.addObserver( (download, arg) -> {
                if(download instanceof DownloadResourceService) {
                    activity.runOnUiThread(() -> {
                        if (arg instanceof BinaryResponse) {
                            byte[] dados = ((BinaryResponse) arg).getData();
                            InputStream input=new ByteArrayInputStream(dados);
                            Bitmap ext_pic = BitmapFactory.decodeStream(input);
                            holder.bindingVH.fotoPerfil.setImageBitmap(ext_pic);
                        } else{
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id1) -> { }).create().show();
                        }

                        activity.getDialog().hide();

                    });
                }
            });
            downloadService.getResource(post.getAutor().getFotoID());
            activity.getDialog().show();
        }

        holder.bindingVH.btnLike.setOnClickListener(
                v -> {
                    if (!holder.bindingVH.getPost().getLikesId().contains(FindFM.getUsuario().getId())) {
                        JsonTypedRequest<ComentarRequest, ResponseBody, ErrorResponse> postRequest = new JsonTypedRequest<>(
                                activity,
                                HttpMethod.GET.getCodigo(),
                                ComentarRequest.class,
                                ResponseBody.class,
                                ErrorResponse.class,
                                HttpUtils.buildUrl(activity.getResources(), "post/like/" + post.getId()),
                                null,
                                (ResponseBody response) -> {
                                },
                                (ErrorResponse error) -> {
                                },
                                (VolleyError error) -> {
                                }
                        );
                        postRequest.setRequest(null);
                        postRequest.execute();
                        holder.bindingVH.getPost().getLikesId().remove(FindFM.getUsuario().getId());
                        holder.bindingVH.getPost().setLikes((Long.parseLong(post.getLikes()) +1));
                        holder.bindingVH.getPost().getLikesId().add(FindFM.getUsuario().getId());
                        holder.bindingVH.btnLike.setText(holder.bindingVH.getPost().getLikes());
                        holder.bindingVH.executePendingBindings();
                    }else {
                        JsonTypedRequest<ComentarRequest, ResponseBody, ErrorResponse> postRequest = new JsonTypedRequest<>(
                                activity,
                                HttpMethod.DELETE.getCodigo(),
                                ComentarRequest.class,
                                ResponseBody.class,
                                ErrorResponse.class,
                                HttpUtils.buildUrl(activity.getResources(), "post/like/" + post.getId()),
                                null,
                                (ResponseBody response) -> {
                                },
                                (ErrorResponse error) -> {
                                },
                                (VolleyError error) -> {
                                }
                        );
                        postRequest.setRequest(null);
                        postRequest.execute();
                        holder.bindingVH.getPost().setLikes((Long.parseLong(post.getLikes()) -1));
                        holder.bindingVH.getPost().getLikesId().remove(FindFM.getUsuario().getId());
                        holder.bindingVH.btnLike.setText(holder.bindingVH.getPost().getLikes());
                        holder.bindingVH.executePendingBindings();
                    }
                }
        );

        holder.bindingVH.setClickListener(v -> {
            if(post.getAutor().getId().equals(FindFM.getUsuario().getId())){
                String path = "CriarPost";
                Bundle param = new Bundle();
                FindFM.getMap().put("post", post);
                param.putString("telaMode", "editavel");
                param.putBoolean("visitante", TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario()));
                Util.open_form_withParam(activity, CriarPost.class, path, param);
            } else {
                String path = "CriarPost";
                Bundle param = new Bundle();
                FindFM.getMap().put("post", post);
                param.putString("telaMode", "visualizar");
                param.putBoolean("visitante", TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario()));
                Util.open_form_withParam(activity, CriarPost.class, path, param);
            }
        });

        if(isVisitante){
            holder.bindingVH.btnLike.setOnClickListener(
                    v -> AlertDialogUtils.newSimpleDialog__OneButton(activity,
                            "Ops!", R.drawable.ic_error,
                            "Essa ação requer que você esteja logado com uma conta\nLogue ou crie uma conta","OK",
                            (dialog, id) -> { }).create().show());
        }

        if(post.getLikesId().contains(FindFM.getUsuario().getId())){
            holder.bindingVH.btnLike.setText(R.string.descurtir);
        }else {
            holder.bindingVH.btnLike.setText(R.string.curtir);
        }

    }

    @Override
    public int getItemCount() {
        if (posts == null) {
            return 0;
        }
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewItemFeedBinding bindingVH;

        ViewHolder(ViewItemFeedBinding binding){
            super(binding.getRoot());
            this.bindingVH = binding;
        }
    }
}