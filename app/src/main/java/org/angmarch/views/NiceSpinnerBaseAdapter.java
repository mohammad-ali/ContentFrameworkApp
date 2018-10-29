package org.angmarch.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kaspian.book.R;
import com.kaspian.book.Settings_Activity;

/**
 * @author angelo.marchesin
 */

@SuppressWarnings("unused")
public abstract class NiceSpinnerBaseAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected int mSelectedIndex;
    protected int mTextColor;
    protected int mBackgroundSelector;

    public NiceSpinnerBaseAdapter(Context context, int textColor, int backgroundSelector) {
        mContext = context;
        mTextColor = textColor;
        mBackgroundSelector = backgroundSelector;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.spinner_list_item, null);


            textView = (TextView) convertView.findViewById(R.id.tv_tinted_spinner);



            convertView.setTag(new ViewHolder(textView));
        } else {
            textView = ((ViewHolder) convertView.getTag()).textView;
        }

        textView.setText(getItem(position).toString());
        if(!Settings_Activity.loadNightMode(mContext)){
            textView.setTextColor(Color.parseColor("#202020"));
        }else{
            textView.setTextColor(Color.parseColor("#ffffff"));
        }



        return convertView;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void notifyItemSelected(int index) {
        mSelectedIndex = index;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract T getItem(int position);

    @Override
    public abstract int getCount();

    public abstract T getItemInDataset(int position);

    protected static class ViewHolder {

        public TextView textView;

        public ViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}
