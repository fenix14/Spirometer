package com.fenix.spirometer.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.fenix.spirometer.util.Constants;

@Entity(primaryKeys = {"name", "isMale"})
public class EstValue extends BaseModel {
    @NonNull
    private String name;

    @ColumnInfo(defaultValue = "true")
    private boolean isMale;

    @NonNull
    private String formula;

    @NonNull
    private float valueR;

    @Ignore
    public EstValue() {
    }

    public EstValue(@NonNull String name, boolean isMale, @NonNull String formula, float valueR) {
        this.name = name;
        this.isMale = isMale;
        this.formula = formula;
        this.valueR = valueR;
    }

    public String getGender() {
        return isMale ? "男" : "女";
    }

    public void setGender(@NonNull String gender) {
        setMale(gender.equals("男"));
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getFormula() {
        return formula;
    }

    public void setFormula(@NonNull String formula) {
        this.formula = formula;
    }

    public float getValueR() {
        return valueR;
    }

    public void setValueR(float valueR) {
        this.valueR = valueR;
    }

    public void setValueR(String valueR) {
        this.valueR = Float.parseFloat(valueR);
    }
}
