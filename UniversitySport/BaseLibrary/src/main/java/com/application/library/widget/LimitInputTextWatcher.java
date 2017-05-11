package com.application.library.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


public class LimitInputTextWatcher implements TextWatcher {

    private EditText editText;
    private String regex;//限制条件
    private String DEFAULT_REGEX = "[^\\u4E00-\\u9FA5]";//默认只能输入中文

    public LimitInputTextWatcher(EditText editText) {
        this.editText = editText;
        this.regex = DEFAULT_REGEX;
    }

    public LimitInputTextWatcher(EditText editText, String regex) {
        this.editText = editText;
        this.regex = regex;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String input = editable.toString();
        String inputStr = clearLimitStr(regex, input);
        editText.removeTextChangedListener(this);
        editable.replace(0, editable.length(), inputStr.trim());
        editText.addTextChangedListener(this);
    }

    private String clearLimitStr(String regex, String input) {
        return input.replaceAll(regex, "");
    }
}
