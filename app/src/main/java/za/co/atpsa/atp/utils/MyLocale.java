package za.co.atpsa.atp.utils;

import java.util.List;
import java.util.Locale;

import za.co.atpsa.atp.entities.Country;

public class MyLocale {

    public static int getLocale(List<Country> countryList) {
        Locale locale = Locale.getDefault();
        for (Country c : countryList) {
            if (c.getCode().equals(locale.getCountry())) {
                return countryList.indexOf(c);
            }
        }
        return 0;
    }
}
