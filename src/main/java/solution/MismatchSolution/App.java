package solution.MismatchSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App { 
    public static void main(String args[]){
        //查询 Q
        String[] Q = {"344", "Makley", "10:30AM", "BIOL"};
        //查询结果 R
        List<Map> R = new ArrayList<Map>();
        Map r1 = new HashMap();
        r1.put("vlca", "0");
        String[] M = {"0.1.2", "0.1.6", "0.1.8.0", "0.2.1"};
        r1.put("nodes", M);
        R.add(r1);
        //阈值
        double τ = 0.9;
        
        PreParse parser = new PreParse(Q);
        HashMap info = parser.parse();
        
        Resolver resolver = new Resolver(Q, R, τ, info);
        resolver.resolve();
        resolver.close();
    }
} 
