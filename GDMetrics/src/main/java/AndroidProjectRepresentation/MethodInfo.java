package AndroidProjectRepresentation;



import com.github.javaparser.ast.body.ModifierSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MethodInfo implements Serializable, JSONSerializable {

    public Set<MethodOfAPI> externalApi = new HashSet<>();
    public Set<MethodOfAPI> androidApi = new HashSet<>();
    public Set<MethodOfAPI> javaApi = new HashSet<>();
    public Set<MethodOfAPI> unknownApi = new HashSet<>();

    public Set<Variable> declaredVars = new HashSet<>();
    public Set<Variable> args = new HashSet<Variable>();

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






    public JSONObject methodInfoToJSON (String classID){
        JSONObject method = new JSONObject();
        method.put("method_id", this.getMethodID());
        method.put("method_name", this.methodName);
        method.put("method_non_acc_mod",  (this.isSynchronized? "#synchronized" : "" )+ (this.isFinal? "#final" : "" )+ (this.isStatic? "#static" : ""  ));
        method.put("method_acc_modifier", this.accessModifier);

        method.put("method_class", classID);
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
        String metId= this.ci!=null ?  this.ci.getSimpleClassID() + "." + this.methodName : this.methodName ;
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
        return this.methodInfoToJSON(classID);
    }

    @Override
    public JSONSerializable fromJSONObject(JSONObject jo) {
        MethodInfo mi = new MethodInfo();
        mi.methodName = (String)jo.get("method_name");
        if (jo.containsKey("method_non_acc_mod") && jo.get("method_non_acc_mod") != null) {
            mi.isFinal = ((String)jo.get("method_non_acc_mod")).contains("final");
            mi.isSynchronized = ((String)jo.get("method_non_acc_mod")).contains("ynchronized");
            mi.isStatic = ((String)jo.get("method_non_acc_mod")).contains("static");
        }

        mi.accessModifier = (String)jo.get("method_acc_modifier");
        JSONArray methodMetrics;
        Iterator var4;
        Object ob;
        JSONObject metric;
        Variable v;
        if (jo.containsKey("method_declared_vars")) {
            methodMetrics = (JSONArray)jo.get("method_declared_vars");
            if (!methodMetrics.isEmpty()) {
                var4 = methodMetrics.iterator();

                while(var4.hasNext()) {
                    ob = var4.next();
                    metric = (JSONObject)ob;
                    if (metric.containsKey("var_type")) {
                        v = (Variable)(new Variable()).fromJSONObject(metric);
                        mi.declaredVars.add(v);
                    }
                }
            }
        }

        String metricName;
        if (jo.containsKey("method_args")) {
            String[] args = ((String)jo.get("method_args")).split("#");
            if (args.length >= 1) {
                String[] var17 = args;
                int var18 = args.length;

                for(int var19 = 0; var19 < var18; ++var19) {
                    metricName = var17[var19];
                    if (!metricName.isEmpty()) {
                        Variable vv = new Variable();
                        vv.isArray = metricName.contains("[]");
                        vv.uuid = var19;
                        vv.type = metricName.replaceAll("\\[", "").replaceAll("\\]","");
                        mi.args.add(vv);
                    }
                }
            }
        }

        if (jo.containsKey("method_metrics")) {
            methodMetrics = (JSONArray)jo.get("method_metrics");
            if (!methodMetrics.isEmpty()) {
                for (Object o : methodMetrics) {
                    JSONObject mt = (JSONObject) o;
                    if (((JSONObject) o).containsKey("mm_metric")) {
                        if (((JSONObject) o).get("mm_metric").equals("loc")) {
                            mi.linesOfCode = ((Long) ((JSONObject) o).get("mm_value")).intValue();
                        }
                        if (((JSONObject) o).get("mm_metric").equals("cc")) {
                            mi.cyclomaticComplexity = ((Long) ((JSONObject) o).get("mm_value")).intValue();
                        }
                        if (((JSONObject) o).get("mm_metric").equals("androidapi")) {
                            String apis = (String) ((JSONObject) o).get("mm_value_text");
                            String[] splits = apis.split("\\|");
                            if (splits.length > 2) {
                                if (splits[1].endsWith("null") && splits[2].equals("()")){

                                }
                                else
                                    mi.androidApi.add(new MethodOfAPI(splits[0].split("\\:").length>1? splits[0].split("\\:")[1]: splits[0] , splits[1] + splits[2]));
                            }
                        }
                        if (((JSONObject) o).get("mm_metric").equals("javaapi")) {
                            String apis = (String) ((JSONObject) o).get("mm_value_text");
                            String[] splits = apis.split("\\|");
                            if (splits.length > 2) {
                                if (splits[1].endsWith("null") && splits[2].equals("()")){

                                }
                                else
                                    mi.javaApi.add(new MethodOfAPI(splits[0].split("\\:").length>1? splits[0].split("\\:")[1]: splits[0] , splits[1] + splits[2]));
                            }
                        }
                        if (((JSONObject) o).get("mm_metric").equals("externalapi")) {
                            String apis = (String) ((JSONObject) o).get("mm_value_text");
                            String[] splits = apis.split("\\|");
                            if (splits.length > 2) {
                                if (splits[1].endsWith("null") && splits[2].equals("()")){

                                }
                                else
                                    mi.externalApi.add(new MethodOfAPI(splits[0].split("\\:").length>1? splits[0].split("\\:")[1]: splits[0] , splits[1] + splits[2]));
                            }
                        }


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

    public void setModifiers(int modifiers) {
        this.isStatic = ModifierSet.isStatic(modifiers);
        this.isSynchronized = ModifierSet.isSynchronized(modifiers);
        this.isFinal = ModifierSet.isFinal(modifiers);
        this.accessModifier = ModifierSet.isPublic(modifiers)? "public" : (ModifierSet.isProtected(modifiers)? "protected" : (ModifierSet.isPrivate(modifiers)? "private": ""));
    }

    @Override
    public String toString() {
        return "MethodInfo{" +
                "externalApi=" + externalApi +
                ", androidApi=" + androidApi +
                ", javaApi=" + javaApi +
                ", unknownApi=" + unknownApi +
                ", declaredVars=" + declaredVars +
                ", args=" + args +
                ", isStatic=" + isStatic +
                ", isSynchronized=" + isSynchronized +
                ", isFinal=" + isFinal +
                ", linesOfCode=" + linesOfCode +
                ", cyclomaticComplexity=" + cyclomaticComplexity +
                ", accessModifier='" + accessModifier + '\'' +
                ", methodName='" + methodName + '\'' +
                ", ci=" + ci +
                '}';
    }
}
