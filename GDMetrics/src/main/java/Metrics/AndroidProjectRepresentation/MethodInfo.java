package Metrics.AndroidProjectRepresentation;



import Metrics.MethodOfAPI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class MethodInfo implements Serializable, JSONSerializable {

    public Set<MethodOfAPI> externalApi = new HashSet<>();
    public Set<MethodOfAPI> androidApi = new HashSet<>();
    public Set<MethodOfAPI> javaApi = new HashSet<>();
    public Set<MethodOfAPI> unknownApi = new HashSet<>();

    public Set<Variable> declaredVars = new HashSet<>();
    public Set<Variable> args = new HashSet<>();

    public boolean isStatic = false;
    public boolean isSynchronized = false;
    public boolean isFinal = false;

    public int linesOfCode = 0;
    public int cyclomaticComplexity = 0;

    public String accessModifier = "public";
    public String methodName = "";

    public ClassInfo ci = null;






    public static JSONObject getMethodMetric (String methodId, String metricName, Number value, String valueText, String methodInvokedId){
        JSONObject jo = new JSONObject();
        jo.put("mm_method", methodId);
        jo.put("mm_metric", metricName);
        jo.put("mm_value", value);
        jo.put("mm_value_text", valueText);
        if (methodInvokedId!=null )
            jo.put("mm_method_invoked", methodInvokedId);
        return jo;
    }






    public JSONObject methodInfoToJSON (){
        JSONObject method = new JSONObject();
        method.put("method_id", this.getMethodID());
        method.put("method_name", this.methodName);
        method.put("method_non_acc_mod",  (this.isSynchronized? "#synchronized" : "" )+ (this.isFinal? "#final" : "" )+ (this.isStatic? "#static" : ""  ));
        method.put("method_acc_modifier", this.accessModifier);

        method.put("method_class", this.ci.getClassID());
        JSONArray methodMetrics = new JSONArray();

        methodMetrics.add(getMethodMetric(this.getMethodID(),"loc", this.linesOfCode, "",null));
        methodMetrics.add(getMethodMetric(this.getMethodID(),"cc", this.cyclomaticComplexity, "",null));
        methodMetrics.add(getMethodMetric(this.getMethodID(),"nr_args", this.args.size(), "",null));
        for (MethodOfAPI moa : this.externalApi){
            String api = "API: " +moa.api + "|method: " + moa.method + "|(";
            for (Variable v : moa.args){
                api+= "#" + v.type +( v.isArray?"[]":"") ;
            }
            api+=")";
            JSONObject metric = getMethodMetric( this.getMethodID(),"externalapi", 0, api , null);
            methodMetrics.add(metric);

        }
        for (MethodOfAPI moa : this.androidApi){
            String api = "API: " +moa.api + "|method: " + moa.method + "|(";
            for (Variable v : moa.args){
                api+= "#" + v.type +( v.isArray?"[]":"") ;
            }
            api+=")";
            JSONObject metric = getMethodMetric( this.getMethodID(),"androidapi", 0, api , null);
            methodMetrics.add(metric);

        }
        for (MethodOfAPI moa : this.javaApi){
            String api = "API: " +moa.api + "|method: " + moa.method + "|(";
            for (Variable v : moa.args){
                api+= "#" + v.type +( v.isArray?"[]":"") ;
            }
            api+=")";
            JSONObject metric = getMethodMetric( this.getMethodID(),"javaapi", 0, api , null);
            methodMetrics.add(metric);

        }

        method.put("method_metrics", methodMetrics);


        JSONArray vars = new JSONArray();
        for (Variable v : this.declaredVars){
            JSONObject var = new JSONObject();
            var.put("var_type", v.type);
            var.put("var_array", v.isArray);
            var.put("var_name", v.varName);
            vars.add(var);
        }
        method.put("method_declared_vars", vars);

        String args = "";
        for (Variable v : this.args){
            args+= v.type + (v.isArray ? "[]" : "") + "#";
        }
        method.put("method_args", args);


        return method;

    }

    public  String getMethodID() {
        String metId= this.ci!=null ? this.ci.classPackage+"."+this.ci.className+"."+ this.methodName : this.methodName ;
        metId +="(";
        for (Variable v : args) {
            metId += "#" + v.type;
        }
        metId +=")";
        return metId;
    }


    public  boolean isInDeclaredVars(String var){
        if (var==null)
            return false ;
        for (Variable v : this.declaredVars){
            if(v==null)
                continue;
            if(v.varName==null)
                continue;
            if (var.equals(v.varName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.methodName.hashCode() + new Integer(this.args.size()).hashCode();
    }

    @Override
    public String toString() {
        return methodName + "  cc -> " + cyclomaticComplexity + " loc -> " + linesOfCode  ;
    }

    // Add correct
    public void addRespectiveAPI(MethodOfAPI x){
        if (!this.declaredVars.contains(new Variable(x.api,""))){
            if(this.ci.classVariables.containsKey(x.api)){
                this.unknownApi.add(new MethodOfAPI(this.ci.classVariables.get(x.api).type,x.method));
                return;
            }
            else {
                for (Variable v : this.args){
                    if (v.varName.equals(x.api)){
                        this.unknownApi.add(new MethodOfAPI(v.type,x.method));
                        return;
                    }

                }
            }
        }
        else {
            for (Variable v : this.declaredVars){
                if (v.varName.equals(x.api)){
                    this.unknownApi.add(new MethodOfAPI(v.type,x.method));
                    return;
                }

            }
        }

        this.unknownApi.add(x);

    }


    public Set<String> getApisUsed(){
        Set <String> l = new HashSet<>();
        for (MethodOfAPI s : this.androidApi) {
            l.add(s.api);
        }
        for (MethodOfAPI s : this.javaApi) {
            l.add(s.api);
        }
        for (MethodOfAPI s : this.externalApi) {
            l.add(s.api);
        }
        return l;
    }

    @Override
    public JSONObject toJSONObject(String classID) {
        return this.methodInfoToJSON();
    }

    @Override
    public JSONSerializable fromJSONObject(JSONObject jo) {
        MethodInfo mi = new MethodInfo();
        mi.methodName = ((String) jo.get("method_name"));
        mi.isFinal = ((String) jo.get("method_non_acc_mod")).contains("final");
        mi.isSynchronized = ((String) jo.get("method_non_acc_mod")).contains("ynchronized");
        mi.isStatic = ((String) jo.get("method_non_acc_mod")).contains("static");
        mi.accessModifier = ((String) jo.get("method_acc_modifier"));

        for (Object o : ((JSONArray) jo.get("method_declared_vars"))){
            JSONObject job = ((JSONObject) o);
            if (job.containsKey("var_type")){
                Variable v = ((Variable) new Variable().fromJSONObject(job));
                mi.declaredVars.add(v);
            }
        }

        for (Object o : ((JSONArray) jo.get("method_declared_vars"))){
            JSONObject job = ((JSONObject) o);
            if (job.containsKey("var_type")){
                Variable v = ((Variable) new Variable().fromJSONObject(job));
                mi.declaredVars.add(v);
            }
        }

        String [] args = ((String) jo.get("method_args")).split("#");
        if (args.length>1){
            for (String s : args){
                if (!s.isEmpty()){
                    Variable v = new Variable();
                    v.isArray = s.contains("[]");
                    v.type = s.replaceAll("\\[\\]","");
                    mi.args.add(v);
                }

            }
        }

        JSONArray methodMetrics = ((JSONArray) jo.get("method_metrics"));
        for (Object ob : methodMetrics){
            JSONObject metric = (JSONObject) ob;

            if (metric.containsKey("metric_name")){
                String metricName = (String) metric.get("metric_name");
                if (metricName.equals("loc")){
                    mi.linesOfCode = ((int) metric.get("mm_value"));
                }
                if (metricName.equals("cc")){
                    mi.cyclomaticComplexity = ((int) metric.get("mm_value"));
                }
                if (metricName.equals("androidapi")){
                    String [] api = ((String) metric.get("mm_value_text")).split("\\|");
                    if(api.length>2){
                        MethodOfAPI moa = new MethodOfAPI(api[0].replace("API:", ""),api[1].replace("method:",""));
                        String [] apiArgs =  (api[2].substring(api[2].indexOf("(")+1,api[2].indexOf(")"))).split("#");
                        if (apiArgs.length>1){
                            for (String s : apiArgs){
                                if (!s.isEmpty()){
                                    Variable v = new Variable();
                                    v.isArray = s.contains("[]");
                                    v.type = s.replaceAll("\\[\\]","");
                                    moa.args.add(v);
                                }

                            }
                        }
                        mi.androidApi.add(moa);
                    }

                }
                if (metricName.equals("javaapi")){
                    String [] api = ((String) metric.get("mm_value_text")).split("\\|");
                    if(api.length>2){
                        MethodOfAPI moa = new MethodOfAPI(api[0].replace("API:", ""),api[1].replace("method:",""));
                        String [] apiArgs =  (api[2].substring(api[2].indexOf("(")+1,api[2].indexOf(")"))).split("#");
                        if (apiArgs.length>1){
                            for (String s : apiArgs){
                                if (!s.isEmpty()){
                                    Variable v = new Variable();
                                    v.isArray = s.contains("[]");
                                    v.type = s.replaceAll("\\[\\]","");
                                    moa.args.add(v);
                                }

                            }
                        }
                        mi.javaApi.add(moa);
                    }

                }
                if (metricName.equals("externalapi")){
                    String [] api = ((String) metric.get("mm_value_text")).split("\\|");
                    if(api.length>2){
                        MethodOfAPI moa = new MethodOfAPI(api[0].replace("API:", ""),api[1].replace("method:",""));
                        String [] apiArgs =  (api[2].substring(api[2].indexOf("(")+1,api[2].indexOf(")"))).split("#");
                        if (apiArgs.length>1){
                            for (String s : apiArgs){
                                if (!s.isEmpty()){
                                    Variable v = new Variable();
                                    v.isArray = s.contains("[]");
                                    v.type = s.replaceAll("\\[\\]","");
                                    moa.args.add(v);
                                }

                            }
                        }
                        mi.externalApi.add(moa);
                    }

                }
            }

        }


        return mi;

    }

    @Override
    public JSONObject fromJSONFile(String pathToJSONFile) {
        JSONParser parser = new JSONParser();
        JSONObject ja = new JSONObject();

        try {
            Object obj = parser.parse(new FileReader(pathToJSONFile));
            JSONObject jsonObject = (JSONObject)obj;
            if (jsonObject.containsKey("method_id")) {
                return jsonObject;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return ja;
    }
}
