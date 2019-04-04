package solution.MismatchSolution;

import java.util.ArrayList;
import java.util.Arrays;
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
        ArrayList<HashMap> suggestedQueries = resolver.resolve();
        resolver.close();
        //测试
        String[][] arries = new String[3][];
        String[] a1 = {"0.1.2", "0.1.6", "0.1.8.0", "0.2.1"};
        String[] a2 = {"0.1.2", "0.1.6", "0.1.8.0"};
        String[] a3 = {"0.1.2", "0.1.6", "0.2.1"};
        arries[0] = a1;
        arries[1] = a2;
        arries[2] = a3;
        
        ArrayList<ArrayList<String>> idArray = new ArrayList<ArrayList<String>>();
        ArrayList<String> arr1 = new ArrayList<String>(Arrays.asList(a1));
        ArrayList<String> arr2 = new ArrayList<String>(Arrays.asList(a2));
        ArrayList<String> arr3 = new ArrayList<String>(Arrays.asList(a3));
        idArray.add(arr1);
        idArray.add(arr2);
        idArray.add(arr3);
    }
} 
