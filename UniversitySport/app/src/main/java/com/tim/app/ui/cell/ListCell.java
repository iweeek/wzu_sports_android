package com.tim.app.ui.cell;

import android.widget.BaseAdapter;

public interface ListCell
{

    public void onGetData(Object data, int position, BaseAdapter adapter);

}
