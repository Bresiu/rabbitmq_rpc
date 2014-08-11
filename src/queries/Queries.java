package queries;

import java.util.HashMap;
import java.util.Map;

public class Queries {

    /**
     * Internal Load
     */
    private static Map<String, String> iTRIMP = new HashMap<String, String>() {
        {
            put("i_trimp_total", "i_trimp_total");
            put("last_load", "activities().last().load(_w_,_x_,_y,_z_)");
            put("avg", "activities().last().fragment().where(time(laterThan(_u_,_v_).ago())).load(_w_,_x_,_y_,_z_)");

        }
    };

    private static Map<String, String> Heart_Rate = new HashMap<String, String>() {
        {
            put("Asd", "asd");

        }
    };

    private static Map<String, String> iTRIM3P = new HashMap<String, String>() {
        {
            put("Asd", "asd");

        }
    };

    private static Map<String, String> iTRI4MP = new HashMap<String, String>() {
        {
            put("Asd", "asd");

        }
    };

    /**
     * External Load
     */


    /**
     * Internal / External Ratios (Fitness)
     */


}
