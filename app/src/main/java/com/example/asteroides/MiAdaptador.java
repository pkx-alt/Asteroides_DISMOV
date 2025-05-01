package com.example.asteroides;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.asteroides.presentacion.MainActivity;

import java.util.List;

public class MiAdaptador extends RecyclerView.Adapter<MiAdaptador.ViewHolder> {
    private LayoutInflater inflador;
    private List<String> lista;

    protected View.OnClickListener onClickListener;

    public void setOnItemClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    public MiAdaptador(Context context, List<String> lista) {
        this.lista = lista;
        inflador = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.elemento_lista, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.titulo.setText(lista.get(i));
//        MainActivity.lectorImagenes.get("https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png", ImageLoader.getImageListener(holder.icon, R.drawable.asteroide1, R.drawable.asteroide3));
        holder.icon.setImageUrl("https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png", MainActivity.lectorImagenes);
    }
    //        switch (Math.round((float)Math.random() * 3)) {
//            case 0:
//                holder.icon.setImageResource(R.drawable.asteroide1);
//                break;
//            case 1:
//                holder.icon.setImageResource(R.drawable.asteroide2);
//                break;
//            default:
//                holder.icon.setImageResource(R.drawable.asteroide3);
//                break;
//        }
    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titulo, subtitulo;
        public NetworkImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            subtitulo = itemView.findViewById(R.id.subtitulo);
            icon = itemView.findViewById(R.id.icono);
        }
    }
}
