package com.pedroquessada.processoseletivofinch.util;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Service
public class ConversorUtil {
    public String getDateToString(Calendar calendar, String formato) {
        SimpleDateFormat s = new SimpleDateFormat(formato);
        return s.format(calendar.getTime());
    }

    public Double getStringToDouble(String texto) {
        return Double.valueOf(texto);
    }
}
