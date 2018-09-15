package Metrics.AndroidProjectRepresentation;



import Metrics.NameExpression;
import com.github.javaparser.ast.body.ModifierSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;


public class ClassInfo implements Serializable, JSONSerializable {

    public boolean isInterface = false;
    public boolean isFinal = false;
    public boolean isAbstract = false;
    public String accessModifier = "public"; //TODO
    public String classPackage = "";
    public String className = "";
    public String extendedClass = "";
    public Set<NameExpression> classImports = new HashSet<>();
    public Map<String, MethodInfo> classMethods = new HashMap<>();
    public Map<String, Variable> classVariables = new HashMap<>();
    public Set< String> interfacesImplemented = new HashSet<>(); // map interface name -> full package definition
    public String appID="";

    @Override
    public int hashCode() {
        return this.className.hashCode();
    }


    public void setModifiers(int modifiers) {
        this.isAbstract = ModifierSet.isAbstract(modifiers);
        this.isFinal = ModifierSet.isFinal(modifiers);
        this.accessModifier = ModifierSet.isPublic(modifiers)? "public" : (ModifierSet.isProtected(modifiers)? "protected" : (ModifierSet.isPrivate(modifiers)? "private": ""));
    }

    public ClassInfo (String appID){

        this.appID = appID;
    }




    public static JSONObject getClassMetric (String classID, String metricName, Number value, String valueText){
        JSONObject jo = new JSONObject();
        jo.put("cm_class", classID);
        jo.put("cm_metric", metricName);
        jo.put("cm_value", value);
        jo.put("cm_value_text", valueText);
        return jo;
    }


    public JSONObject classInfoToJSON(String appID){

        JSONObject jo = new JSONObject();
        jo.put("class_id", this.getClassID());
        jo.put("class_app", appID);
        jo.put("class_is_interface", this.isInterface);
        jo.put("class_non_acc_mod", (this.isFinal? "final" : "" )+ (this.isAbstract? "#abstract" : ""  ));
        jo.put("class_name", this.className);
        jo.put("class_package", this.classPackage);
        jo.put("class_superclass", this.extendedClass);
        jo.put("class_acc_modifier",this.accessModifier );
        JSONArray imports = new JSONArray();
        for (NameExpression ne : this.classImports){
            JSONObject jj = new JSONObject();
            //jj.put("import", ne.qualifier+"."+"name" );
            jj.put("import_name", ne.qualifier+"."+"name");
            jj.put("import_class", this.getClassID());
            imports.add(jj);
        }
        jo.put("class_imports", imports);
        JSONArray methods = new JSONArray();
        for (MethodInfo mi : this.classMethods.values()){
            JSONObject m= mi.methodInfoToJSON(this.getClassID());
            methods.add(m);

        }
        jo.put("class_methods", methods);

        JSONArray vars = new JSONArray();
        for (Variable v : this.classVariables.values()){
            JSONObject var = new JSONObject();
            var.put("var_type", v.type);
            var.put("var_array", v.isArray);
            var.put("var_isStatic", v.isStatic);
            var.put("var_isFinal", v.isFinal);
            var.put("var_isVolatile", v.isVolatile);
            var.put("var_isTransient", v.isTransient);
            vars.add(var);
        }
        jo.put("class_vars", vars);
        String ifaces = "";
        for (String s : this.interfacesImplemented){
            ifaces+=s+"#";
        }
        jo.put("class_implemented_ifaces",ifaces );
        return jo;
    }


    public String getFullClassName(){
        return classPackage +"." + className;
    }

    public MethodInfo getMethod(String methodName){
        for (String s : this.classMethods.keySet()){
            if (s.contains(methodName));
                return this.classMethods.get(s);
        }
        return null;
    }


    public String getClassID(){

        return this.appID + "#" + this.classPackage + "." + this.className ;
    }


    @Override
    public JSONObject toJSONObject(String appID) {
        return classInfoToJSON(appID);
    }

    @Override
    public JSONSerializable fromJSONObject(JSONObject jo) {
        ClassInfo classe = new ClassInfo("");
        classe.appID = ((String) jo.get("class_id")).split("#")[0];
        classe.className = ((String) jo.get("class_name"));
        classe.classPackage = ((String) jo.get("class_package"));
        classe.isInterface = ((boolean) jo.get("class_is_interface"));
        classe.isAbstract = ((String) jo.get("class_non_acc_mod")).contains("abstract");
        classe.isFinal = ((String) jo.get("class_non_acc_mod")).contains("final");
        classe.extendedClass = ((String) jo.get("class_superclass"));
        classe.accessModifier = ((String) jo.get("class_acc_modifier"));

        for (String  s:((String) jo.get("class_implemented_ifaces")).split("#")){
            classe.interfacesImplemented.add(s);
        }

        for (Object o : ((JSONArray) jo.get("class_imports"))){
            JSONObject job = ((JSONObject) o);
            if (job.containsKey("import_name")){
                String [] imp = ((String) job.get("import_name")).split("\\.");
                if (imp.length>1){
                    classe.classImports.add(new NameExpression(imp[0], imp[1]));
                }

            }
        }

        if (jo.containsKey("class_methods")){
            JSONArray jj = ((JSONArray) jo.get("class_methods"));
            if (!jo.isEmpty()){
                for (Object o : jj){
                    JSONObject job = ((JSONObject) o);
                    if (job.containsKey("method_id")){
                        MethodInfo mi = new MethodInfo();
                        mi = ((MethodInfo) new MethodInfo().fromJSONObject(job));
                        mi.ci = classe;
                        classe.classMethods.put(mi.getMethodID(),mi );
                    }
                }
            }

        }

        if (jo.containsKey("class_vars")){
            JSONArray jj = ((JSONArray) jo.get("class_vars"));
            if (!jj.isEmpty()){
                for (Object o : jj){
                    JSONObject job = ((JSONObject) o);
                    if (job.containsKey("var_type")){
                        Variable v = ((Variable) new Variable().fromJSONObject(job));
                        classe.classVariables.put(v.varName, v);
                    }
                }
            }
        }


        return classe;

    }

    @Override
    public JSONObject fromJSONFile(String pathToJSONFile) {
        JSONParser parser = new JSONParser();
        JSONObject ja = new JSONObject();

        try {
            Object obj = parser.parse(new FileReader(pathToJSONFile));
            JSONObject jsonObject = (JSONObject)obj;
            if (jsonObject.containsKey("class_id")) {
                return jsonObject;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return ja;

    }
}
