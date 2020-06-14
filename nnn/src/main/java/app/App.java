package app;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class App {

    private static Integer SEQ = 0;
    private static final String F = "---";
    private static final String R = "###";
    private static final String C = ",";
    private static final String CONST_SIGN = "CCCCC";
    private static final String METHOD_SIGN = "MMMMM";
    private static final String CLASS_SEQ_DIGIT = "12";
    private static final String CLASS_GRP_DIGIT = "8";
    private static final String CLASS_GRPSEQ_DIGIT = "4";
    private static final String SIGNATURE_GRP_DIGIT = "2";
    private static final String SIGNATURE_GRPSEQ_DIGIT = "2";

    private static final String A1 = "行番号";
    private static final String COL_NAME_SEPARATOR = "-";
    private static final String COL_SEPARATOR = "\t";
    private static final String COL_VALUE_SEPARATOR = ",";

    private static final ClassLoader parent = ClassLoader.getSystemClassLoader();;

    private static final Map<Integer,String> CONST_COL_NAME_LIST = mkColHashMap(IntStream.rangeClosed(0,1).boxed().collect(toList()), Arrays.asList(
            "クラス名"
            ,"定数名"
    ));
    private static final Map<Integer,String> METHOD_COL_NAME_LIST = mkColHashMap(IntStream.rangeClosed(0,9).boxed().collect(toList()), Arrays.asList(
            "クラス名"
            ,"アクセス修飾子"
            ,"戻り値の型"
            ,"メソッド名"
            ,"可変長引数があるか"
            ,"引数の個数"
            ,"型パラメータリスト"
            ,"型パラメータ記号リスト"
            ,"引数の型リスト"
            ,"仮引数の変数名リスト"
    ));
    public static class CrossTab{
        private String tblHead;//表頭
        private Map<String,String> tblBody;//表側
        public void setTblHead(String tblHead) {
            this.tblHead=tblHead;
        }
        public void setTblBody(Map<String,String> tblBody) {
            this.tblBody=tblBody;
        }
        public String getTblHead() {
            return tblHead;
        }
        public Map<String, String> getTblBody() {
            return tblBody;
        }
    }

    private static Map<Integer,String> mkColHashMap(List<Integer> k,List<String> v){
        if(k.size()!=v.size()){
            return new HashMap<>();
        }else{
            return k.stream().collect(Collectors.toMap(e->e, e->v.get(e)));
        }
    }
    private static List<List<String>> getClassInfo(Integer grp,Map<Class<?>,String> m ) {
        return unnest(wrapperClassInfo(grp,m)).entrySet().stream()
                .map(e-> flattenList(new ArrayList<>(Arrays.asList(e.getKey().split(F))
                            .subList(0, e.getKey().split(F).length - 1)),e.getValue()))
                .collect(Collectors.toList());
    }
    private static List<List<String>> rearrange(List<List<String>> ll) {
        int row = ll.size();
        return IntStream.range(0, row).boxed().parallel().map(e -> Arrays.asList(
                ll.get(e).get(0).replace(COL_NAME_SEPARATOR,R) // /home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup###1.10.2.jar
                , ll.get(e).get(2) // 00000001
                , ll.get(e).get(3) // 0001
                , ll.get(e).get(1) + COL_NAME_SEPARATOR + ll.get(e).get(4) // MMMMM-00
                , ll.get(e).get(1) + COL_NAME_SEPARATOR + ll.get(e).get(4) + COL_NAME_SEPARATOR + ll.get(e).get(5) // MMMMM-00-クラス名
                , ll.get(e).get(6) // org.jsoup.Connection$Request
        )).collect(Collectors.toList());
    }
    private static void crossTablation(CrossTab crossTab,List<List<String>> rearrangeList){
        crossTable(rearrangeList,4,6,crossTab);
    }
    private static void outputHeadRecord(CrossTab crossTab){
        Stream.of(crossTab.getTblHead()).forEach(e-> System.out.println(e));
    }
    private static void outputBodyRecord(CrossTab crossTab){
        crossTab.getTblBody().entrySet().stream()
                .sorted(Comparator.comparing(e->e.getKey()))
                .forEach(e->{
                    ++SEQ;
                    System.out.println(String.format("%0"+CLASS_SEQ_DIGIT+"d",SEQ)
                            +COL_NAME_SEPARATOR
                            +e.getKey().replace(R,COL_NAME_SEPARATOR)
                            +COL_SEPARATOR
                            +e.getValue());
                });
    }
    @SafeVarargs
    private static <E> List<E> flattenList(Collection<E>... liz){
        return Arrays.stream(liz).flatMap(e -> e.stream()).collect(Collectors.toList());
    }

    private static Map<String, Set<String>> crossTableCreateTableHeadPreProcess(List<List<String>> ll,Integer endGrpColIdx){
        int row = ll.size();
        return IntStream.range(0,row).boxed().collect(Collectors.groupingBy(i->ll.get(i).get(endGrpColIdx-1)
                ,Collectors.mapping(i->ll.get(i).get(endGrpColIdx),Collectors.toSet())));
    }

    private static Map<String, Map<String,String>> crossTableCreateTableHeadPostProcess(List<List<String>> ll,Integer endGrpColIdx){
        int row = ll.size();
        return null;
    }

    /**
    *<pre>
    *INPUT:
    *       ARG1:２次元リスト
    *       ARG2:グループ化項目列の最終列インデックス番号
    *       ARG3:グループ化対象列のインデックス番号
    *<pre/>
    *<pre>
    *CMD:
    *       次のプロセスでグルーピングできるようにキーとバリューにグループ化項目列の最終列をともに持たせるようなデータ構造に変換
    *       バリューの方でエントリ索引できるようにマップにしておく
    *<pre/>
    *<pre>
    *OUTPUT:
    *       グループ化項目列の最終列をキーに持ち、バリューに以下（※１）のマップを持つマップを返却
    *       ※１ グループ化項目列の最終列をキーに持ち、グループ化対象列をバリューに持つマップ
    *<pre/>
    *<pre>
    *EXCEPTION:
    *       NONE
    *<pre/>
    * */
    private static Map<String, Map<String,String>> crossTableCreateTableSidePreProcess(List<List<String>> ll,Integer endGrpColIdx,Integer grpColIdx){
        int row = ll.size();
        return IntStream.range(0,row).boxed()
                // /home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup###1.10.2.jar-00000001-0001-MMMMM-09
                .collect(Collectors.groupingBy(i->ll.get(i).subList(0,endGrpColIdx).stream().collect(Collectors.joining(COL_NAME_SEPARATOR))
                        // { MMMMM-09 -> arg0}
                        ,Collectors.groupingBy(i->ll.get(i).get(endGrpColIdx-1),Collectors.mapping(i->ll.get(i).get(grpColIdx-1),Collectors.joining(COL_VALUE_SEPARATOR)))));
    }

    /**
     *<pre>
     *INPUT:
     *      ARG1:
     *              グループ化項目列の最終列をキーに持ち、バリューに以下（※１）のマップを持つマップを返却
     *              ※１グループ化項目列の最終列をキーに持ち、グループ化対象列をバリューに持つマップ
     *      ARG2:グループ化項目列の最終列インデックス番号
     *      ARG3:グループ化対象列のインデックス番号
     *<pre/>
     *<pre>
     *CMD:
     *      キー側ではグループ化項目列のうち最終列を除いた項目列をキーに変換
     *      バリュー側ではグループ化対象列をカラムセパレータで集約化
     *<pre/>
     *<pre>
     *OUTPUT:
     *      グループ化項目列のうち最終列を除いた項目列をキーに持ち、グループ化対象列をカラムセパレータで集約化した値をバリューに持つマップ
     *<pre/>
     *<pre>
     *EXCEPTION:
     *       NONE
     *<pre/>
     * */
    private static Map<String,String> crossTableCreateTableSideMidProcess(Map<String, Map<String,String>> preBody,Integer endGrpColIdx) {
        return preBody.entrySet().stream().sorted(Comparator.comparing(e->e.getKey()))
                .collect(Collectors.groupingBy(e->Arrays.asList(e.getKey().split(COL_NAME_SEPARATOR)).subList(0,endGrpColIdx-1).stream().collect(Collectors.joining(COL_NAME_SEPARATOR)) // /home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup###1.10.2.jar-00000001-0001-MMMMM-06 --> /home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup###1.10.2.jar-00000001-0001
                        ,Collectors.mapping(e->e.getValue().values().stream().limit(1).collect(Collectors.joining())
                                ,Collectors.joining(COL_SEPARATOR))));
    }



    private static CrossTab crossTable(List<List<String>> ll,Integer endGrpColIdx,Integer grpColIdx,CrossTab crossTab){

        Map<String, Set<String>> ms = crossTableCreateTableHeadPreProcess(ll,endGrpColIdx);

        //行番号	MMMMM-00-クラス名	MMMMM-01-アクセス修飾子	MMMMM-02-戻り値の型	MMMMM-03-メソッド名	MMMMM-04-可変長引数があるか	MMMMM-05-引数の個数	MMMMM-06-型パラメータリスト	MMMMM-07-型パラメータ記号リスト	MMMMM-08-引数の型リスト	MMMMM-09-仮引数の変数名リスト
        String tblHead = A1+COL_SEPARATOR+ms.entrySet().stream()
                .flatMap(e->e.getValue().stream())
                .sorted(Comparator.comparing(e->Arrays.asList(e.split(COL_NAME_SEPARATOR)).subList(0,endGrpColIdx-2).stream().collect(Collectors.joining())))
                .collect(Collectors.joining(COL_SEPARATOR));

        //表側
        //PreProcess
        // key --> /home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup###1.10.2.jar-00000001-0001-MMMMM-09 ,value -->{ MMMMM-09 -> arg0}

        Map<String, Map<String,String>> preBody = crossTableCreateTableSidePreProcess(ll,endGrpColIdx,grpColIdx);

        //MidProcess
//        "/home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup###1.10.2.jar-00000001-0021" -> "org.jsoup.Connection$Request\tpublic abstract\tjava.lang.String\tcookie\tfalse\t1\t\t\tjava.lang.String\targ0"
//        "/home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup###1.10.2.jar-00000001-0020" -> "org.jsoup.Connection$Request\tpublic abstract\tT\tcookie\tfalse\t2\t\t\tjava.lang.String,java.lang.String\targ0,arg1"
//        "/home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup###1.10.2.jar-00000001-0023" -> "org.jsoup.Connection$Request\tpublic abstract\tT\tmethod\tfalse\t1\t\t\torg.jsoup.Connection$Method\targ0"
//        "/home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup###1.10.2.jar-00000001-0001" -> "org.jsoup.Connection$Request\tpublic abstract\torg.jsoup.Connection$Request\ttimeout\tfalse\t1\t\t\tint\targ0"
//        "/home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup###1.10.2.jar-00000001-0022" -> "org.jsoup.Connection$Request\tpublic abstract\torg.jsoup.Connection$Method\tmethod\tfalse\t0\t\t\t\t"
//        Map<String,String> midBody = preBody.entrySet().stream().sorted(Comparator.comparing(e->e.getKey()))
//                .collect(Collectors.groupingBy(e->Arrays.asList(e.getKey().split(COL_NAME_SEPARATOR)).subList(0,endGrpColIdx-1).stream().collect(Collectors.joining(COL_NAME_SEPARATOR)) // /home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup###1.10.2.jar-00000001-0001-MMMMM-06 --> /home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup###1.10.2.jar-00000001-0001
//                        ,Collectors.mapping(e->e.getValue().values().stream().limit(1).collect(Collectors.joining())
//                                ,Collectors.joining(COL_SEPARATOR))));


        Map<String,String> midBody = crossTableCreateTableSideMidProcess(preBody,endGrpColIdx);

        Integer mx = tblHead.length()-tblHead.replace(COL_SEPARATOR,"").length()+1;

        //PostProcess
        //定数の場合とメソッドの場合で取得できる列数が異なるので、列数の多い方に寄せている
        Map<String,String> tblBody = midBody.entrySet().stream()
                .collect(Collectors.toMap(e->e.getKey()
                        ,e->(e.getValue().length()-e.getValue().replace(COL_SEPARATOR,"").length()+1)==METHOD_COL_NAME_LIST.size()?
                                COL_SEPARATOR.repeat(mx-(e.getValue().length()-e.getValue().replace(COL_SEPARATOR,"").length()+1)-1)+e.getValue()
                                :e.getValue()+COL_SEPARATOR.repeat(mx-(e.getValue().length()-e.getValue().replace(COL_SEPARATOR,"").length()+1)-1)));

        //Set
        crossTab.setTblHead(tblHead);
        crossTab.setTblBody(tblBody);
        return crossTab;
    }
    private static Map<String,List<String>> unnest(Map<String,List<String>> m){
        Map<String,List<String>> rt = new LinkedHashMap<>();
        for(Map.Entry<String,List<String>> entry : m.entrySet()){
            int mx = entry.getValue().size();
            for(int i =0;i<mx;i++){
                List<String> liz = Arrays.asList(entry.getValue().get(i).split(C));
                int cnt = liz.size();
                for(int j=0;j<cnt;j++){
                    rt.put(
                            entry.getKey()+F+String.format("%0"+SIGNATURE_GRP_DIGIT+"d",i)+F+String.format("%0"+SIGNATURE_GRPSEQ_DIGIT+"d",j)
                            ,Arrays.asList(
                                    entry.getKey().contains(CONST_SIGN)?CONST_COL_NAME_LIST.get(i):METHOD_COL_NAME_LIST.get(i)
                                    ,liz.get(j).replace(R,C)));
                }
            }
        }
        return rt;
    }
    private static Map<Method,Class<?>> getMethodInfo(Class<?> e) throws NoClassDefFoundError, VerifyError, IncompatibleClassChangeError, InternalError{
        List<Method> l = Arrays.asList(e.getMethods());
        return IntStream.rangeClosed(0,l.size()-1).boxed().parallel().collect(Collectors.toMap(i->l.get(i),i->e));
    }
    private static Map<Field,Class<?>> getFieldInfo(Class<?> e) throws NoClassDefFoundError, VerifyError, IncompatibleClassChangeError, InternalError{
        List<Field> l = Arrays.asList(e.getFields());
        return IntStream.rangeClosed(0,l.size()-1).boxed().parallel().collect(Collectors.toMap(i->l.get(i),i->e));
    }
    private static Map<String,List<String>> wrapperFieldInfo(Integer grp,Map.Entry<Class<?>,String> entryClass,Class<?> clz)  throws NoClassDefFoundError, VerifyError, IncompatibleClassChangeError, InternalError{
        Map<String,List<String>> rt = new LinkedHashMap<>();
        int cnt = 0;
        for(Map.Entry<Field,Class<?>> entryField : getFieldInfo(clz).entrySet()){
            ++cnt;
            rt.put(entryClass.getValue()+F+CONST_SIGN+F+String.format("%0"+CLASS_GRP_DIGIT+"d",grp)+F+String.format("%0"+CLASS_GRPSEQ_DIGIT+"d",cnt)
                    ,Arrays.asList(
                            entryField.getValue().getName()//クラス名
                            ,entryField.getKey().getName()//定数名
                    )
            );
        }
        return rt;
    }
    private static Map<String,List<String>> wrapperMethodInfo(Integer grp,Map.Entry<Class<?>,String> entryClass,Class<?> clz){
        Map<String,List<String>> rt = new LinkedHashMap<>();
        int cnt = 0;
        for(Map.Entry<Method,Class<?>> entryMethod : getMethodInfo(clz).entrySet()){
            ++cnt;
            rt.put(entryClass.getValue()+F+METHOD_SIGN+F+String.format("%0"+CLASS_GRP_DIGIT+"d",grp)+F+String.format("%0"+CLASS_GRPSEQ_DIGIT+"d",cnt)
                    ,Arrays.asList(
                            entryMethod.getValue().getName()//クラス名
                            , Modifier.toString(entryMethod.getKey().getModifiers())//アクセス修飾子
                            ,entryMethod.getKey().getGenericReturnType().getTypeName()//戻り値の型
                            ,entryMethod.getKey().getName()//メソッド名
                            ,String.valueOf(entryMethod.getKey().isVarArgs())//可変長引数があるか
                            ,String.valueOf(entryMethod.getKey().getParameterCount())//引数の個数
                            ,Arrays.stream(entryMethod.getKey().getTypeParameters()).flatMap(e->Arrays.asList(e.getBounds()).stream()).map(ee->ee.getTypeName().replace(C,R)).collect(Collectors.joining(C))//型パラメータリスト
                            ,Arrays.stream(entryMethod.getKey().getTypeParameters()).map(e->e.getTypeName().replace(C,R)).collect(Collectors.joining(C))//型パラメータで使用しているアルファベット大文字記号リスト
                            ,Arrays.stream(entryMethod.getKey().getGenericParameterTypes()).map(e->e.getTypeName().replace(C,R)).collect(Collectors.joining(C)) //引数の型リスト
                            ,Arrays.stream(entryMethod.getKey().getParameters()).map(e->e.getName()).collect(Collectors.joining(C))//仮引数の変数名リスト
                    ));
        }
        return rt;
    }
    private static Map<String,List<String>> wrapperClassInfo(Integer grp,Map<Class<?>,String> classsInfoMap){
        Map<String,List<String>> rt = new LinkedHashMap<>();
        for(Map.Entry<Class<?>,String> entryClass : classsInfoMap.entrySet()){
            Class<?> clz = entryClass.getKey();
            rt.putAll(wrapperFieldInfo(grp,entryClass,clz));
            rt.putAll(wrapperMethodInfo(grp,entryClass,clz));
        }
        return rt;
    }
    private static Set<File> getJarFileList(File dir) throws IOException {
        Path baseDir = Paths.get(dir.getAbsolutePath());
        String targetExtension = "jar";

        String includeExtensionPtn = ("(?i)^.*\\." + Pattern.quote(targetExtension) + "$"); //完全一致パタンを作成している

        return Files.walk(baseDir)
                .parallel()
                .map(e -> e.toFile())
                .filter(e ->e.isFile())
                .filter(e ->e.getAbsolutePath().matches(includeExtensionPtn))
                .filter(e->!e.getAbsolutePath().contains("sources"))
                .collect(Collectors.toSet());
    }
    private static URLClassLoader newClassLoader(Set<File> files) {
        URL[]urls = files.stream().map(file->getURL(file)).collect(Collectors.toList()).toArray(new URL[files.size()]);
        return new URLClassLoader(urls, parent);
    }
    private static URL getURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public static void main(String... args) throws IOException {
//        String defaultBaseDir = "/home/kuraine/.m2/repository/";

//        Set<File> jarFileList = new LinkedHashSet<>();
        Set<File> jarFileList = new LinkedHashSet(){{add(new File("/home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup-1.10.2.jar"));}};

//        if(args.length>0){
//            jarFileList.addAll(Arrays.asList(args).stream().map(jarFileName->new File(jarFileName)).collect(Collectors.toList()));
//        }else{
//            jarFileList = getJarFileList(new File(defaultBaseDir));
//        }

        ClassLoader classLoader = newClassLoader(jarFileList);

        int jarFileListCnt = jarFileList.size();
        int jarFileClassCnt = 0;


        List<Map<Class<?>,String>> loadClassJarFileNameMapList = new LinkedList<>();

        List<String> classLoadList = new LinkedList<>();
        List<String> classLoadSkipList = new LinkedList<>();
        List<String> classExecuteList = new LinkedList<>();
        List<String> classExecuteSkipList = new LinkedList<>();

        Map<String,List<String>> classLoadDoneResult = new LinkedHashMap<>();
        Map<String,List<String>> classLoadSkipResult = new LinkedHashMap<>();
        Map<String,List<String>> classExecuteDoneResult = new LinkedHashMap<>();
        Map<String,List<String>> classExecuteSkipResult = new LinkedHashMap<>();

        for (File f : jarFileList) {

            JarFile jarFile = new JarFile(f.getPath());
            List<JarEntry> jarEntries = Collections.list(jarFile.entries());

            for (JarEntry jarEntry : jarEntries) {
                if (jarEntry.getName().endsWith(".class")) {
                    jarFileClassCnt++;
                    String className = jarEntry.getName().replace('/', '.').replaceAll(".class$", "");
                    try {

                        Class<?> loadClass = classLoader.loadClass(className);
                        Map<Class<?>,String> loadClassJarFileNameMap = new LinkedHashMap(){{
                            put(loadClass,f.getPath());
                        }};
                        loadClassJarFileNameMapList.add(loadClassJarFileNameMap);

                        classLoadList.add(className);

                    }catch (ClassNotFoundException | NoClassDefFoundError | VerifyError | IncompatibleClassChangeError | InternalError e){
                        //クラスパスロード時のハンドリング
                        classLoadSkipList.add(className);
                    }
                }
            }
            classLoadDoneResult.put(f.getPath(),classLoadList);
            classLoadSkipResult.put(f.getPath(),classLoadSkipList);
        }

        CrossTab crossTab = new CrossTab();

        int cnt = loadClassJarFileNameMapList.size();
        int grp = 0;
        List<List<String>> classInfoList = new LinkedList<>();
        for(int i=0;i<cnt;i++){
            try{
                grp++;
                classInfoList = getClassInfo(grp,loadClassJarFileNameMapList.get(i));
                List<List<String>> rearrangeList = rearrange(classInfoList);
                crossTablation(crossTab,rearrangeList);
                if(i==0){
                    outputHeadRecord(crossTab);
                }else{
                    outputBodyRecord(crossTab);
                }

                classExecuteList.addAll(classInfoList.stream().map(r->r.get(0)).collect(Collectors.toList()));

            }catch (NoClassDefFoundError | VerifyError | IncompatibleClassChangeError | InternalError e){
                //実行時のハンドリング
                classExecuteSkipList.addAll(classInfoList.stream().filter(r->r.contains("クラス名")).map(r->r.get(6)).collect(Collectors.toSet()));
            }
            classExecuteDoneResult.put(classInfoList.stream().map(r->r.get(0)).limit(1).collect(Collectors.joining()), classExecuteList);
            classExecuteSkipResult.put(classInfoList.stream().map(r->r.get(0)).limit(1).collect(Collectors.joining()), classExecuteSkipList);
        }

        System.out.printf(
                "%s\t%s\n" +
                        "%s\t%s\n" +
                        "%s\t%s\n" +
                        "%s\t%s\n" +
                        "%s\t%s\n" +
                        "%s\t%s\n" +
                        "\n"
                ,"jarFileListCnt",jarFileListCnt
                ,"jarFileClassCnt",jarFileClassCnt
                ,"jarFileClassLoadDoneCnt",classLoadList.size()
                ,"jarFileClassLoadSkipCnt",classLoadSkipList.size()
                ,"jarFileClassExecuteDoneCnt",classExecuteList.size()
                ,"jarFileClassExecuteSkipCnt",classExecuteSkipList.size()
        );
    }
}