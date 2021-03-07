package com.fenix.spirometer.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.BaseModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class CustomExcel<T extends BaseModel> extends FrameLayout {
    private LinearLayout llHeader;
    private RecyclerView rvList;
    private boolean isListStripe;
    private int columns;
    private int weightSum;
    private BaseExcelAdapter adapter;
    private int[] headerGravities;
    private int headerMinHeight;

    public CustomExcel(Context context) {
        this(context, null);
    }

    public CustomExcel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomExcel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomExcel, defStyleAttr, 0);
        int headerBg = typedArray.getColor(R.styleable.CustomExcel_header_background, 0);
        headerMinHeight = typedArray.getDimensionPixelSize(R.styleable.CustomExcel_header_min_height, 0);
        isListStripe = typedArray.getBoolean(R.styleable.CustomExcel_stripe_list, true);

        View view = LayoutInflater.from(context).inflate(R.layout.widget_excel, this);
        llHeader = view.findViewById(R.id.widget_excel_header);
        llHeader.setBackgroundColor(headerBg);
        rvList = view.findViewById(R.id.id_custom_excel_list);
    }

    private void setupRow(LinearLayout row, @NonNull String[] header) {
        TextView textView;
        for (int i = 0; i < columns; i++) {
            textView = (TextView) row.getChildAt(i);
            textView.setVisibility(View.VISIBLE);
            textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, headerGravities[i]));
            textView.setText(header[i]);
        }
        row.setWeightSum(weightSum);
        row.setMinimumHeight(headerMinHeight);
        row.requestLayout();
    }

    public BaseExcelAdapter setup(@NonNull String[] headers, @NonNull int[] headerGravities, @NonNull List<T> dataList, @NonNull List<Method> getters, boolean isShowIndex, boolean isShowMSkipMark) {
        columns = headers.length;
        if (columns != headerGravities.length || columns > llHeader.getChildCount()) {
            throw new IllegalArgumentException("headers = " + columns + "gravities = " + headerGravities.length);
        }
        this.headerGravities = headerGravities;

        weightSum = 0;
        for (int i : headerGravities) {
            weightSum += i;
        }

        setupRow(llHeader, headers);
        rvList.setLayoutManager(new LinearLayoutManager(rvList.getContext()));

        adapter = new BaseExcelAdapter(dataList, getters, isShowIndex, isShowMSkipMark);
        adapter.setStripe(isListStripe);
        rvList.setAdapter(adapter);
        return adapter;
    }

    public class BaseExcelAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private boolean isStripe = false;
        final List<T> dataList;
        final List<Method> getters;
        final boolean isShowIndex;
        final boolean isShowMSkipMark;
        private int columnCount;
        private OnRowStateChangeListener<T> listener;

        public BaseExcelAdapter(@NonNull List<T> dataList, @NonNull List<Method> getters, boolean isShowIndex, boolean isShowMSkipMark) {
            this.dataList = dataList;
            this.getters = getters;
            this.isShowIndex = isShowIndex;
            this.isShowMSkipMark = isShowMSkipMark;
            columnCount = getters.size();
            if (isShowIndex) {
                columnCount++;
            }
            if (isShowMSkipMark) {
                columnCount++;
            }
        }

        public int getColumnCount() {
            return columnCount;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_excel_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            TextView item;
            String[] rowContents = new String[columnCount];
            int start = 0;
            if (isShowIndex) {
                rowContents[start++] = String.valueOf(position);
            }
            if (isShowMSkipMark) {
                rowContents[columnCount - 1] = holder.llRow.getContext().getString(R.string.item_suffix);
            }
            String content;
            for (int i = 0; i < getters.size(); i++) {
                try {
                    content = getters.get(i).invoke(dataList.get(position)).toString();
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    content = "ERR";
                }
                rowContents[start++] = content;
                //Log.d("hff", "rowContents = " + Arrays.toString(rowContents));
            }
            holder.llRow.setBackgroundResource((isStripe && position % 2 == 1) ? R.color.light_gray : R.color.white);
            setupRow(holder.llRow, rowContents);
            if (listener != null) {
                listener.onBindViewHolder(holder.llRow, dataList.get(position), position);
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public void setStripe(boolean stripe) {
            isStripe = stripe;
        }

        public void setOnRowStateChangedListener(OnRowStateChangeListener<T> listener) {
            this.listener = listener;
        }

        public void addRow(T data) {
            dataList.add(data);
            notifyItemInserted(dataList.size());
        }

        public void reload(List<T> data) {
            if (data == null || data.isEmpty()) {
                dataList.clear();
                notifyItemRemoved(0);
            } else {
                dataList.clear();
                dataList.addAll(data);
                notifyItemInserted(0);
            }
        }

        public void clear() {
            dataList.clear();
            notifyDataSetChanged();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llRow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            llRow = itemView.findViewById(R.id.widget_excel_row);
        }
    }

    public interface OnRowStateChangeListener<T> {
        void onBindViewHolder(LinearLayout row, T data, int position);
    }
}
