package android_demo_app.babator.com.androiddemoapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * A View holder class for the RecyclerView holding the players list
 */
public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    final TextView tvName;
    private final IHolderClicks mListener;

    /**
     * Constructor
     * @param view The View
     * @param listener a listener reference
     */
    public CustomViewHolder(View view, IHolderClicks listener) {
        super(view);
        mListener = listener;
        view.setOnClickListener(this);
        this.tvName = (TextView) view.findViewById(R.id.tvName);
    }

    @Override
    public void onClick(View v){
        mListener.onItemClick(this, getAdapterPosition());
    }

    /**
     * An interface to pass item clicked
     */
    public interface IHolderClicks{
        void onItemClick(CustomViewHolder caller, int position);
    }
}
