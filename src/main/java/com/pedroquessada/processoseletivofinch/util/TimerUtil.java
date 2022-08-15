package com.pedroquessada.processoseletivofinch.util;

import com.pedroquessada.processoseletivofinch.exceptions.TimerUtilException;
import org.springframework.stereotype.Service;

@Service
public class TimerUtil {
    public void aguardarMinutos(long minutos) throws TimerUtilException {
        try {
            Thread.sleep(minutos * 60 * 1000);
        }
        catch (InterruptedException e) {
            throw new TimerUtilException(minutos, "minutos");
        }
    }

    public void aguardarSegundos(long segundos) throws TimerUtilException {
        try {
            Thread.sleep(segundos * 1000);
        }
        catch (InterruptedException e) {
            throw new TimerUtilException(segundos, "segundos");
        }
    }
}
