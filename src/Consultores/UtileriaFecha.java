/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Consultores;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ReCS!Gua
 */
public class UtileriaFecha
{
    public static  String ConvertirString(Date fecha)
    {
        DateFormat df;
        String     dateString = null;
        df = new SimpleDateFormat("yyyy-MM-dd");
        dateString = df.format(fecha);

        return dateString;
    }
}
