package com.nsa.teamtwo.welshpharmacy.util;

import java.util.Locale;

public class KeyValueHelper {
    public static final String KEY_PREFERENCES = "preferences";

    public static final String KEY_LANGUAGE = "language";
    public static final String DEFAULT_LANGUAGE = Locale.getDefault().getLanguage();

    public static final String KEY_SHOW_SETUP = "show_setup";
    public static final boolean DEFAULT_SHOW_SETUP = true;

    public static final String KEY_CUSTOM_LOCATION = "postcode";

    public static final String KEY_RADIUS = "radius";
    public static final int DEFAULT_RADIUS = 150;

    //GP details
    public static final String KEY_GP_NAME = "gpName";
    public static final String KEY_GP_ADDRESS = "gpAddress";
    public static final String KEY_GP_TELPHONE = "gpTelephone";
    public static final String KEY_DR_NAME = "doctorName";

    // Theme
    public static final String KEY_THEME = "theme";
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;

    public static final String KEY_THEME_AUTO = "autoTheme";
    public static final boolean DEFAULT_THEME_AUTO = false;


    public static final String KEY_FILTER_LOGIC = "filterLogic";
    public static final String DEFAULT_FILTER_LOGIC = "AND";

    //Pages
    public static final String KEY_PAGE = "page";
}