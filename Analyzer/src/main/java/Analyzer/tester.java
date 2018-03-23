package Analyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.Writer;



/**
 * Created by rrua on 30/05/17.
 */
public class tester {

    public static void write (Writer w, List<String> l) throws IOException {

        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String value: l)
        {
            if(!first){
                sb.append(",");
                sb.append(value);
            }
            else {
                sb.append(value);
                first=false;
            }
        }

        sb.append("\n");
        w.append(sb.toString());
    }



}

