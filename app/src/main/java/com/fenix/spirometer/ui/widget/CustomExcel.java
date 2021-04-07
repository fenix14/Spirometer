package com.fenix.spirometer.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.BaseModel;
import com.fenix.spirometer.util.ModelUtils;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CustomExcel<T extends BaseModel> extends FrameLayout implements SearchView.OnQueryTextListener {
    public static final String TAG_COLUMN_SUFFIX = "suffix";
    public static final int SUFFIX_TYPE_SKIP = 0;
    public static final int SUFFIX_TYPE_DEL = 1;
    public static final int SUFFIX_TYPE_EDIT = 2;
    public static final int PREFIX_TYPE_INDEX = 0;
    public static final int PREFIX_TYPE_CHECKBOX = 1;
    private final LinearLayout llHeader;
    private final RecyclerView rvList;
    private final boolean isListStripe;
    private SearchView searchView;
    private BaseExcelAdapter adapter;
    private int columns;
    private int weightSum;
    private int[] headerGravities;
    private final int headerMinHeight;
    private int suffixType;
    private int prefixType;
    private final int tabItemsId;
    private TabLayout tabLayout = null;
    private LinearLayoutManager listManager;
    private List<T> mDataList = new ArrayList<>();
    private int selectedPosition = -1;
    private int chosenPosition = -1;

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
        suffixType = typedArray.getInt(R.styleable.CustomExcel_last_column_type, -1);
        prefixType = typedArray.getInt(R.styleable.CustomExcel_prefix_type, -1);
        boolean isShowSearch = typedArray.getBoolean(R.styleable.CustomExcel_show_search, false);
        tabItemsId = typedArray.getResourceId(R.styleable.CustomExcel_tab_items, 0);

        View view = LayoutInflater.from(context).inflate(R.layout.widget_excel, this);
        llHeader = view.findViewById(R.id.widget_excel_header);
        llHeader.setBackgroundColor(headerBg);
        rvList = view.findViewById(R.id.id_custom_excel_list);
        if (tabItemsId > 0) {
            tabLayout = view.findViewById(R.id.tabs);
        }
        if (isShowSearch) {
            searchView = view.findViewById(R.id.searchView);
            searchView.setVisibility(View.VISIBLE);
        }
    }

    private void setupRow(LinearLayout row, @NonNull String[] header) {
        View textView;
        for (int i = 0; i < columns; i++) {
            textView = row.getChildAt(i);
            textView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, headerGravities[i]);
            if (i == columns - 1) {
                textView.setTag(TAG_COLUMN_SUFFIX);
                if (suffixType == SUFFIX_TYPE_EDIT && !header[i].isEmpty()) {
                    layoutParams.setMargins(60, 40, 30, 40);
                }
            }
            textView.setLayoutParams(layoutParams);
            if (textView instanceof CheckBox) {
                CheckBox checkBox = ((CheckBox) textView);
                if (prefixType == PREFIX_TYPE_CHECKBOX) {
                    checkBox.setButtonDrawable(ContextCompat.getDrawable(getContext(), R.drawable.checkbox_button));
                    checkBox.setText("");
                } else {
                    checkBox.setButtonDrawable(null);
                    checkBox.setText(header[i]);
                }
            } else {
                ((TextView) textView).setText(header[i]);
            }
        }
        row.setWeightSum(weightSum);
        row.setMinimumHeight(headerMinHeight);
    }

    public void setup(@NonNull String[] headers, @NonNull int[] headerGravities, @NonNull List<T> data, @NonNull List<Method> getters) {
        columns = headers.length;
        if (columns != headerGravities.length || columns > llHeader.getChildCount()) {
            throw new IllegalArgumentException("headers = " + columns + "gravities = " + headerGravities.length);
        }
        this.headerGravities = headerGravities;
        mDataList = data;
        weightSum = 0;
        for (int i : headerGravities) {
            weightSum += i;
        }

        setupRow(llHeader, headers);
        listManager = new LinearLayoutManager(rvList.getContext());
        rvList.setLayoutManager(listManager);

        adapter = new BaseExcelAdapter(mDataList, getters);
        adapter.setStripe(isListStripe);
        rvList.setAdapter(adapter);
        setupTabLayout();

        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
        }
    }

    private void setupTabLayout() {
        if (tabItemsId <= 0) {
            return;
        }
        String[] tabItemContents = getContext().getResources().getStringArray(tabItemsId);
        if (tabItemContents == null || tabItemContents.length == 0) {
            return;
        }

        tabLayout.setVisibility(View.VISIBLE);
        for (int i = 0; i < tabItemContents.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabItemContents[i]).setTag(i));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int sizeForEach = mDataList.size() / tabLayout.getTabCount();
                listManager.scrollToPositionWithOffset(tab.getPosition() * sizeForEach, 0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        rvList.setOnScrollChangeListener((view, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int sizeForEach = mDataList.size() / tabLayout.getTabCount();
            tabLayout.setScrollPosition(listManager.findLastVisibleItemPosition() / sizeForEach, 0, true);
        });
    }

    @Override
    public boolean onQueryTextSubmit(String searchContent) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchContent) {
        if (mDataList == null) {
            return false;
        }
        Log.d("hff", "searchContent = " + searchContent);
        if (TextUtils.isEmpty(searchContent)) {
            adapter.reload(mDataList);
        } else {
            List<T> searchList = new ArrayList<>();
            for (T data : mDataList) {
                if (ModelUtils.contains(data, adapter.getters, searchContent)) {
                    searchList.add(data);
                }
            }
            adapter.reload(searchList);
        }
        return true;
    }

    public class BaseExcelAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private boolean isStripe = false;
        final List<T> dataList;
        final List<Method> getters;
        private int columnCount;
        private OnRowStateChangeListener listener;

        public BaseExcelAdapter(@NonNull List<T> dataList, @NonNull List<Method> getters) {
            this.dataList = dataList;
            this.getters = getters;
            columnCount = getters.size();
            if (prefixType >= 0) {
                columnCount++;
            }
            if (suffixType >= 0) {
                columnCount++;
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_excel_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            String[] rowContents = new String[columnCount];
            int start = 0;
            switch (prefixType) {
                case PREFIX_TYPE_INDEX:
                    rowContents[start++] = String.valueOf(position);
                    break;
                case PREFIX_TYPE_CHECKBOX:
                    rowContents[start++] = "";
                    break;
                default:
                    break;
            }

            switch (suffixType) {
                case SUFFIX_TYPE_SKIP:
                    rowContents[columnCount - 1] = getContext().getString(R.string.item_suffix_skip);
                    break;
                case SUFFIX_TYPE_DEL:
                    rowContents[columnCount - 1] = getContext().getString(R.string.item_suffix_del);
                    break;
                case SUFFIX_TYPE_EDIT:
                    rowContents[columnCount - 1] = getContext().getString(R.string.item_suffix_edit);
                    break;
                default:
                    break;
            }

            String content;
            for (int i = 0; i < getters.size(); i++) {
                try {
                    content = String.valueOf(getters.get(i).invoke(dataList.get(position)));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    content = "ERR";
                }
                rowContents[start++] = content;
            }
            holder.llRow.setBackgroundResource((isStripe && position % 2 == 1) ? R.color.light_gray : R.color.white);
            setupRow(holder.llRow, rowContents);
            ((CheckBox)holder.llRow.getChildAt(0)).setChecked(selectedPosition == position || chosenPosition == position);
            if (isEditMode()) {
                holder.llRow.setOnClickListener(view -> {
                    if (selectedPosition >= 0 && mDataList.get(selectedPosition).equals(dataList.get(position))) {
                        selectedPosition = -1;
                    } else if (position != chosenPosition){
                        selectedPosition = position;
                    }
                    notifyDataSetChanged();
                });
            } else if (listener != null) {
                listener.onBindViewHolder(holder.llRow, position);
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public List<T> getDataList() {
            return dataList;
        }

        public void setStripe(boolean stripe) {
            isStripe = stripe;
        }

        public void setOnRowStateChangedListener(OnRowStateChangeListener listener) {
            this.listener = listener;
        }

        public void reload(List<T> data) {
            if (data == null || data.isEmpty()) {
                dataList.clear();
                notifyDataSetChanged();
            } else {
                dataList.clear();
                dataList.addAll(data);
                notifyDataSetChanged();
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

    public void setOnRowStateChangedListener(OnRowStateChangeListener listener) {
        adapter.setOnRowStateChangedListener(listener);
    }

    public interface OnRowStateChangeListener {
        void onBindViewHolder(LinearLayout row, int position);
    }

    public void reload(List<T> data) {
        mDataList = data;
        adapter.reload(mDataList);
    }

    public T getData(int position) {
        return adapter.getDataList().get(position);
    }

    public void switchMode(boolean isCheckbox) {
        int type = isCheckbox ? PREFIX_TYPE_CHECKBOX : PREFIX_TYPE_INDEX;
        if (type != prefixType) {
            prefixType = type;
            adapter.notifyDataSetChanged();
        }
    }

    public boolean isEditMode() {
        return prefixType == PREFIX_TYPE_CHECKBOX;
    }

    public void choose(T t) {
        chosenPosition = mDataList.indexOf(t);
        if (isEditMode()) {
            adapter.notifyDataSetChanged();
        } else {
            switchMode(true);
        }
    }

    public Pair<T, T> getChosen() {
        if (chosenPosition < 0 || chosenPosition > mDataList.size()
                || selectedPosition < 0 || selectedPosition > mDataList.size()) {
            Log.d("hff", "Invalid chosen ids: " + chosenPosition + ", " + selectedPosition);
            return null;
        }

        return new Pair<>(mDataList.get(chosenPosition), mDataList.get(selectedPosition));
    }
}
