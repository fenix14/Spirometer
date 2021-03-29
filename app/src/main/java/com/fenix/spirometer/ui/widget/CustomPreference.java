package com.fenix.spirometer.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.fenix.spirometer.R;
import com.google.android.material.internal.DescendantOffsetUtils;

import java.util.Arrays;
import java.util.List;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;
import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

public class CustomPreference extends Preference {
    boolean isNecessary;
    String title;
    private EditText etContent;
    private TextView tvContent;
    private RadioGroup rgContent;
    private TextView etvContent;
    private int contentType;
    private final int choicesId;
    private String content;
    private int editType;
    private final boolean isShowBoundary;
    private int chosenRadioButtonId = R.id.rb_first;
    private AdapterView.OnItemSelectedListener spinnerSelectedListener;

    public CustomPreference(Context context) {
        this(context, null);
    }

    public CustomPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Preference);
        isNecessary = ta.getBoolean(R.styleable.Preference_necessary, false);
        isShowBoundary = ta.getBoolean(R.styleable.Preference_show_boundary, false);
        contentType = ta.getInt(R.styleable.Preference_content_type, 0);
        title = ta.getString(R.styleable.Preference_title);
        choicesId = ta.getResourceId(R.styleable.Preference_choices, 0);
        content = ta.getString(R.styleable.Preference_content_value);
        editType = ta.getInt(R.styleable.Preference_android_inputType, 1);
        setLayoutResource(R.layout.widget_preference_row);
        ta.recycle();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        TextView tvTitle = (TextView) holder.findViewById(R.id.header_title);
        tvTitle.setText(title);
        tvTitle.setSingleLine(isSingleLineTitle());
        if (isNecessary) {
            holder.findViewById(R.id.pref_necessary).setVisibility(View.VISIBLE);
        }
        switch (contentType) {
            case 0: // TextView
                tvContent = (TextView) holder.findViewById(R.id.pref_content_text);
                tvContent.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(content)) {
                    tvContent.setText(content);
                }
                if (isShowBoundary) {
                    tvContent.setBackgroundResource(R.drawable.bg_stroke_search);
                }
                break;
            case 1: // EditText
                etContent = (EditText) holder.findViewById(R.id.pref_content_edit);
                etContent.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(content)) {
                    etContent.setText(content);
                }
                if (isShowBoundary) {
                    ConstraintLayout container = (ConstraintLayout) holder.findViewById(R.id.pref_container);
                    container.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                }
                etContent.setInputType(editType);
                if (editType == (InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT)) {
                    etContent.setLines(5);
                }
                break;
            case 2: // RadioGroup
                if (choicesId > 0) {
                    rgContent = (RadioGroup) holder.findViewById(R.id.pref_content_radio);
                    rgContent.setVisibility(View.VISIBLE);
                    String[] choices = rgContent.getContext().getResources().getStringArray(choicesId);
                    RadioButton rbFirst = rgContent.findViewById(R.id.rb_first);
                    rbFirst.setText(choices[0]);
                    RadioButton rbSecond = rgContent.findViewById(R.id.rb_second);
                    rbSecond.setText(choices[1]);
                    rgContent.check(chosenRadioButtonId);
                }
                break;
            default:
                break;
        }
        super.onBindViewHolder(holder);
    }

    public String getInput() {
        String content;
        switch (contentType) {
            case 0: // TextView
                content = tvContent.getText().toString().trim();
                break;
            case 1: // EditText
                content = etContent.getText().toString().trim();
                break;
            case 2: // RadioGroup
                RadioButton rbSelected = rgContent.findViewById(rgContent.getCheckedRadioButtonId());
                content = rbSelected == null ? "" : rbSelected.getText().toString().trim();
                break;
            default:
                content = "";
                break;
        }
        return content;
    }

    public boolean isNecessary() {
        return isNecessary;
    }

    public <T extends View> T getContentView() {
        T t = null;
        switch (contentType) {
            case 0: // TextView
                t = (T) tvContent;
                break;
            case 1: // EditText
                t = (T) etContent;
                break;
            case 2: // RadioGroup
                t = (T) rgContent;
                break;
            default:
                return null;
        }
        return t;
    }

    public void setContent(String content) {
        switch (contentType) {
            case 0: // TextView
                this.content = content;
                if (tvContent != null && tvContent.getVisibility() == View.VISIBLE) {
                    tvContent.setText(content);
                }
            case 1: // EditText
                this.content = content;
                if (etContent != null && etContent.getVisibility() == View.VISIBLE) {
                    etContent.setText(content);
                }
                break;
            default:
                break;
        }
    }

    /**
     *
     * @param id ResId for TextView/EditText, [0, 1] for RadioGroup(0:left, 1:right)
     */
    public void setContent(int id) {
        switch (contentType) {
            case 0: // TextView
            case 1: // EditText
                setContent(getContext().getString(id));
                break;
            case 2: // RadioGroup
                chosenRadioButtonId = id == 0 ? R.id.rb_first : R.id.rb_second;
                if (rgContent != null && rgContent.getVisibility() == View.VISIBLE) {
                    rgContent.check(chosenRadioButtonId);
                }
            default:
                break;
        }
    }

    public void setContentType(int type) {
        if (type >= 0 && type <= 3) {
            contentType = type;
        }
    }

    public void setTitle(String content) {
        this.content = content;
        if (tvContent != null && tvContent.getVisibility() == View.VISIBLE) {
            tvContent.setText(content);
        }
    }
}
