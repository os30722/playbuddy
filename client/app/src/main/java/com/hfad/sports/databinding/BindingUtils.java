package com.hfad.sports.databinding;

import androidx.databinding.InverseMethod;

import com.hfad.sports.R;

public class BindingUtils {

    public static String selectedToGender(int id){
        if(id == R.id.male_button) return "Male";
        if(id == R.id.female_button) return "Female";
        return "Male";
    }

    @InverseMethod("selectedToGender")
    public static int genderToSelected(String gender){
        if (gender.equals("")) return -1;
        return (gender.equals("Male")) ? R.id.male_button : R.id.female_button;
    }
}
