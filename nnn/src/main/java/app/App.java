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

import static java.util.stream.Collectors.toList;

public class App {

    //定数だけを持つクラス
    //メソッドだけを持つクラス
    //定数とメソッドの両方を持つクラス

    private static final String PROGRAM_VERSION = "X-X-X";

    private static final String F = "---";
    private static final String R = "###";
    private static final String C = ",";
    private static final String CONST_SIGN = "CCCCC";
    private static final String METHOD_SIGN = "MMMMM";
    private static final String CLASS_GRP_DIGIT = "%08d";
    private static final String CLASS_GRP_SEQ_DIGIT = "%04d";

    private static final String TARGET_EXTENSION = "jar";
    private static final String INCLUDE_EXTENSION_PATTERN = ("(?i)^.*\\." + Pattern.quote(TARGET_EXTENSION) + "$"); //完全一致パタンを作成している
    private static final String USER_HOME_DIR = System.getProperty("user.home");

    private static final String OPTION_ARGUMENTS_MAVEN = "--maven";
    private static final String OPTION_ARGUMENTS_GRADLE = "--gradle";
    private static final String OPTION_ARGUMENTS_KOTLIN = "--kotlin";
    private static final String OPTION_ARGUMENTS_SCALA = "--scala";

    private static final String REPOSITORY_BASE_DIR_MAVEN = "/.m2/repository";
    private static final String REPOSITORY_BASE_DIR_GRADLE = "/.gradle/caches/modules-2/files-2.1";
    private static final String REPOSITORY_BASE_DIR_KOTLIN = "/.sdkman/candidates/kotlin/current/lib";
    private static final String REPOSITORY_BASE_DIR_SCALA = "/.sdkman/candidates/scala/current/lib";
    private static String DEFAULT_BASE_DIR = USER_HOME_DIR + REPOSITORY_BASE_DIR_MAVEN;

    private static final String OPTION_ARGUMENTS_CONSTANT = "--constant";
    private static final String OPTION_ARGUMENTS_METHOD = "--method";
    private static String DEFAULT_OUTPUT = OPTION_ARGUMENTS_CONSTANT;

    private static final String COLUMN_SEPARATOR = "\t";

    private static final ClassLoader parent = ClassLoader.getSystemClassLoader();

    private static final String JARFILE_NAME = "ジャーファイル名";
    private static final String IS_CONSTANT_OR_METHOD_KEY_NAME = "定数かメソッドか";
    private static final String CLASS_NO = "クラス番号";
    private static final String CLASS_NAME = "クラス名";
    private static final String CLASS_SEQ_NO = "クラスシーケンス番号";
    private static final String CONSTANT_NAME = "定数名";
    private static final String ACCESS_PRIVILEGE = "アクセス修飾子";
    private static final String RETURN_TYPE = "戻り値の型";
    private static final String METHOD_NAME = "メソッド名";
    private static final String EXISTS_VARIABLE_ARGS = "可変長引数があるか";
    private static final String ARGS_CNT = "引数の個数";
    private static final String TYPE_PARAMETER_LIST = "型パラメータリスト";
    private static final String TYPE_PARAMETER_SIGN_LIST = "型パラメータ記号リスト";
    private static final String ARGS_TYPE_LIST = "引数の型リスト";
    private static final String ARGS_TYPE_VARIABLE_NAME_LIST = "仮引数の変数名リスト";

    private static final List<String> OUTPUT_HEADER_COMMON_COLUMN_NAME_LIST = new LinkedList(){{
        add(JARFILE_NAME);
        add(IS_CONSTANT_OR_METHOD_KEY_NAME);
        add(CLASS_NO);
        add(CLASS_SEQ_NO);
        add(CLASS_NAME);
    }};
    private static final List<String> OUTPUT_HEADER_CONSTANT_COLUMN_NAME_LIST = new LinkedList(){{
        addAll(OUTPUT_HEADER_COMMON_COLUMN_NAME_LIST);
        add(CONSTANT_NAME);
    }};
    private static final List<String> OUTPUT_HEADER_METHOD_COLUMN_NAME_LIST = new LinkedList(){{
        addAll(OUTPUT_HEADER_COMMON_COLUMN_NAME_LIST);
        add(ACCESS_PRIVILEGE);
        add(RETURN_TYPE);
        add(METHOD_NAME);
        add(EXISTS_VARIABLE_ARGS);
        add(ARGS_CNT);
        add(TYPE_PARAMETER_LIST);
        add(TYPE_PARAMETER_SIGN_LIST);
        add(ARGS_TYPE_LIST);
        add(ARGS_TYPE_VARIABLE_NAME_LIST);
    }};
    private static List<List<String>> getClassInfo(Integer grp,Map<Class<?>,String> m ) {
        List<List<String>> rt = wrapperClassInfo(grp,m).entrySet().stream()
                                    .map(e-> flattenList(new ArrayList<>(Arrays.asList(e.getKey().split(F))),e.getValue()))
                                    .collect(Collectors.toList());
        return rt;
    }
    @SafeVarargs
    private static <E> List<E> flattenList(Collection<E>... liz){
        return Arrays.stream(liz).flatMap(e -> e.stream()).collect(Collectors.toList());
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
            rt.put(entryClass.getValue()+F+CONST_SIGN+F+String.format(CLASS_GRP_DIGIT,grp)+F+String.format(CLASS_GRP_SEQ_DIGIT,cnt)
                    ,Arrays.asList(
                            entryField.getValue().getName() //クラス名
                            ,entryField.getKey().getName() //定数名
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
            rt.put(entryClass.getValue()+F+METHOD_SIGN+F+String.format(CLASS_GRP_DIGIT,grp)+F+String.format(CLASS_GRP_SEQ_DIGIT,cnt)
                    ,Arrays.asList(
                            entryMethod.getValue(). getName() //クラス名
                            ,Modifier.toString(entryMethod.getKey().getModifiers()) //アクセス修飾子
                            ,entryMethod.getKey().getGenericReturnType().getTypeName() //戻り値の型
                            ,entryMethod.getKey().getName() //メソッド名
                            ,String.valueOf(entryMethod.getKey().isVarArgs()) //可変長引数があるか
                            ,String.valueOf(entryMethod.getKey().getParameterCount()) //引数の個数
                            ,Arrays.stream(entryMethod.getKey().getTypeParameters()).flatMap(e->Arrays.asList(e.getBounds()).stream()).map(ee->ee.getTypeName().replace(C,R)).collect(Collectors.joining(C)) //型パラメータリスト
                            ,Arrays.stream(entryMethod.getKey().getTypeParameters()).map(e->e.getTypeName().replace(C,R)).collect(Collectors.joining(C)) //型パラメータで使用しているアルファベット大文字記号リスト
                            ,Arrays.stream(entryMethod.getKey().getGenericParameterTypes()).map(e->e.getTypeName().replace(C,R)).collect(Collectors.joining(C)) //引数の型リスト
                            ,Arrays.stream(entryMethod.getKey().getParameters()).map(e->e.getName()).collect(Collectors.joining(C)) //仮引数の変数名リスト
                    ));
        }
        return rt;
    }
    private static Map<String,List<String>> wrapperClassInfo(Integer grp,Map<Class<?>,String> classInfoMap){
        Map<String,List<String>> rt = new LinkedHashMap<>();
        for(Map.Entry<Class<?>,String> entryClass : classInfoMap.entrySet()){
            Class<?> clz = entryClass.getKey();
            rt.putAll(wrapperFieldInfo(grp,entryClass,clz));
            rt.putAll(wrapperMethodInfo(grp,entryClass,clz));
        }
        return rt;
    }
    private static Set<File> getJarFileList(File dir) throws IOException {
        Path baseDir = Paths.get(dir.getAbsolutePath());

        return Files.walk(baseDir)
                .parallel()
                .map(e -> e.toFile())
                .filter(e ->e.isFile())
                .filter(e ->e.getAbsolutePath().matches(INCLUDE_EXTENSION_PATTERN))
                .filter(e->!e.getAbsolutePath().contains("sources"))
                .collect(Collectors.toSet());
    }
    private static URLClassLoader newClassLoader(Set<File> files) {
        URL[] urls = files.stream().map(file->getURL(file)).collect(Collectors.toList()).toArray(new URL[files.size()]);
        return new URLClassLoader(urls, parent);
    }
    private static URL getURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    private static void Usage(){
        System.out.println("Usageだよーん\n" +
                "java -jar jardump-"+PROGRAM_VERSION+"-SNAPSHOT.jar --maven\n" +
                "or\n" +
                "java -jar jardump-"+PROGRAM_VERSION+"-SNAPSHOT.jar --gradle\n" +
                "or\n" +
                "java -jar jardump-"+PROGRAM_VERSION+"-SNAPSHOT.jar --kotlin\n" +
                "or\n" +
                "java -jar jardump-"+PROGRAM_VERSION+"-SNAPSHOT.jar --scala\n" +
                "or\n" +
                "java -jar jardump-"+PROGRAM_VERSION+"-SNAPSHOT.jar --scala --method\n" +
                "or\n" +
                "java -jar jardump-"+PROGRAM_VERSION+"-SNAPSHOT.jar --scala --constant\n" +
                "or\n" +
                "java -jar jardump-"+PROGRAM_VERSION+"-SNAPSHOT.jar "+ USER_HOME_DIR +"/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup-1.10.2.jar " + USER_HOME_DIR + "/.m2/repository/org/apache/commons/commons-lang3/3.11/commons-lang3-3.11.jar\n" +
                "or\n" +
                "java -jar jardump-"+PROGRAM_VERSION+"-SNAPSHOT.jar "+ USER_HOME_DIR +"/.m2/repository/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar\n" +
                ""
        );
        System.exit(0);
    }
    public static void main(String... args) throws IOException {

        List<String> cmdLineArgs = Arrays.asList(args);

        Set<File> jarFileList = new LinkedHashSet<>();

        //出力情報の制御
        for(String arg : cmdLineArgs){
            switch (arg){
                case OPTION_ARGUMENTS_CONSTANT:
                    DEFAULT_OUTPUT = OPTION_ARGUMENTS_CONSTANT;
                    break;
                case OPTION_ARGUMENTS_METHOD:
                    DEFAULT_OUTPUT = OPTION_ARGUMENTS_METHOD;
                    break;
                default:
                    break;
            }
        }

        if(0 == cmdLineArgs.stream().filter(e->e.matches(INCLUDE_EXTENSION_PATTERN)).collect(Collectors.toSet()).size()) {
            //コマンドライン引数にjarファイルを１つも含まない場合
            for(String arg : cmdLineArgs){
                switch (arg){
                    case OPTION_ARGUMENTS_MAVEN :
                        DEFAULT_BASE_DIR = USER_HOME_DIR + REPOSITORY_BASE_DIR_MAVEN;
                        break;
                    case OPTION_ARGUMENTS_GRADLE:
                        DEFAULT_BASE_DIR = USER_HOME_DIR + REPOSITORY_BASE_DIR_GRADLE;
                        break;
                    case OPTION_ARGUMENTS_KOTLIN:
                        DEFAULT_BASE_DIR = USER_HOME_DIR + REPOSITORY_BASE_DIR_KOTLIN;
                        break;
                    case OPTION_ARGUMENTS_SCALA:
                        DEFAULT_BASE_DIR = USER_HOME_DIR + REPOSITORY_BASE_DIR_SCALA;
                        break;
                    default:
                        Usage();
                        break;
                }
            }

            jarFileList = getJarFileList(new File(DEFAULT_BASE_DIR));

        }else{
            //コマンドライン引数にjarファイルを１つ以上含む場合
            jarFileList.addAll(cmdLineArgs.stream().filter(e->e.matches(INCLUDE_EXTENSION_PATTERN)).collect(Collectors.toSet())
                    .stream().map(jarFileName->new File(jarFileName)).collect(Collectors.toList()));
        }

        ClassLoader classLoader = newClassLoader(jarFileList);

        int jarFileListCnt = jarFileList.size();
        int jarFileClassCnt = 0;

        // サマリ情報格納時、jarFile単位で管理したいので、ネスト浅い箇所にjarFileを持つように定義
        Map<String,List<Map<Class<?>,String>>> classLoadedDoneCanExecuteClassListMap = new LinkedHashMap<>();

        List<String> classLoadList = new LinkedList<>();
        List<String> classLoadSkipList = new LinkedList<>();
        List<String> classExecuteList = new LinkedList<>();
        List<String> classExecuteSkipList = new LinkedList<>();

        Map<String,List<String>> classLoadDoneResult = new LinkedHashMap<>();
        Map<String,List<String>> classLoadSkipResult = new LinkedHashMap<>();
        Map<String,List<String>> classExecuteDoneResult = new LinkedHashMap<>();
        Map<String,List<String>> classExecuteSkipResult = new LinkedHashMap<>();

        Map<String,Map<String,Set<String>>> classLoadSkipCauseSummaryInfo = new LinkedHashMap<>();
        Map<String,Map<String,Set<String>>> classExecuteSkipCauseSummaryInfo = new LinkedHashMap<>();

        for (File f : jarFileList) {

            String jarFileName = f.getPath();

            JarFile jarFile = new JarFile(jarFileName);

            List<JarEntry> jarEntries = Collections.list(jarFile.entries());

            Map<String,Set<String>> classLoadSkipCauseDetailInfo = new LinkedHashMap<>();

            List<Map<Class<?>,String>> classLoadedDoneCanExecuteClassList = new LinkedList<>();

            for (JarEntry jarEntry : jarEntries) {

                if (jarEntry.getName().endsWith(".class")) {

                    jarFileClassCnt++;

                    String tryToLoadClassName = jarEntry.getName().replace('/', '.').replaceAll(".class$", "");

                    try {

                        Class<?> loadClass = classLoader.loadClass(tryToLoadClassName);

                        // 実行時にjarFileが必要なので、持ち回っている
                        Map<Class<?>,String> classLoadedDoneCanExecuteClassMap = new LinkedHashMap(){{
                            put(loadClass ,jarFileName);
                        }};

                        classLoadedDoneCanExecuteClassList.add(classLoadedDoneCanExecuteClassMap);

                        classLoadList.add(tryToLoadClassName);

                    }catch (Throwable e){

                        // 依存関係が足りなかったクラス名ではなくて、ロード使用してできなかったクラス名を格納
                        classLoadSkipList.add(tryToLoadClassName);

                        String className = e.getMessage().replace('/','.');
                        String exceptionClassName = e.getClass().getName();

                        if(classLoadSkipCauseDetailInfo.containsKey(exceptionClassName)){

                            classLoadSkipCauseDetailInfo.get(exceptionClassName).add(className);

                        }else{

                            classLoadSkipCauseDetailInfo.put(exceptionClassName,new LinkedHashSet<>(Arrays.asList(className)));

                        }
                    }
                }
            }

            classLoadedDoneCanExecuteClassListMap.put(f.getPath(),classLoadedDoneCanExecuteClassList);
            classLoadSkipCauseSummaryInfo.put(f.getPath(),classLoadSkipCauseDetailInfo);
            classLoadDoneResult.put(f.getPath(),classLoadList);
            classLoadSkipResult.put(f.getPath(),classLoadSkipList);
        }

        Map<String,Set<String>> classExecuteCauseDetailInfo = new LinkedHashMap<>();

        List<List<String>> classInfoList;
        List<List<String>> classConstantInfoList;
        List<List<String>> classMethodInfoList;

        int grp = 0;

        // 実行時はロードできたクラスファイル件数分で実施
        for(Map.Entry<String,List<Map<Class<?>,String>>> entry : classLoadedDoneCanExecuteClassListMap.entrySet()){

            String jarFile = entry.getKey();

            for(Map<Class<?>,String> classMetaInfo :entry.getValue()){

                try{

                    grp++;

                    if(grp == 1){
                        // ヘッダ行の出力

                        if(DEFAULT_OUTPUT == OPTION_ARGUMENTS_CONSTANT){

                            System.out.println(OUTPUT_HEADER_CONSTANT_COLUMN_NAME_LIST.stream().collect(Collectors.joining(COLUMN_SEPARATOR)));

                        }else if(DEFAULT_OUTPUT == OPTION_ARGUMENTS_METHOD){

                            System.out.println(OUTPUT_HEADER_METHOD_COLUMN_NAME_LIST.stream().collect(Collectors.joining(COLUMN_SEPARATOR)));

                        }else{

                            Usage();

                        }
                    }

                    classInfoList = getClassInfo(grp,classMetaInfo);

                    if(DEFAULT_OUTPUT == OPTION_ARGUMENTS_CONSTANT){

                        classConstantInfoList = classInfoList.stream().filter(e->e.size()==OUTPUT_HEADER_CONSTANT_COLUMN_NAME_LIST.size()).collect(toList());
                        classConstantInfoList.stream().forEach(e-> System.out.println(e.stream().collect(Collectors.joining(COLUMN_SEPARATOR))));

                    }else if(DEFAULT_OUTPUT == OPTION_ARGUMENTS_METHOD){

                        classMethodInfoList = classInfoList.stream().filter(e->e.size()==OUTPUT_HEADER_METHOD_COLUMN_NAME_LIST.size()).collect(toList());
                        classMethodInfoList.stream().forEach(e-> System.out.println(e.stream().collect(Collectors.joining(COLUMN_SEPARATOR))));

                    }else{

                        Usage();

                    }

                    classExecuteList.addAll(classInfoList.stream().collect(Collectors.groupingBy(list->list.get(OUTPUT_HEADER_COMMON_COLUMN_NAME_LIST.indexOf(CLASS_NAME)))).keySet());

                    if(classExecuteDoneResult.containsKey(jarFile)){

                        classExecuteDoneResult.get(jarFile).addAll(classInfoList.stream().collect(Collectors.groupingBy(list->list.get(OUTPUT_HEADER_COMMON_COLUMN_NAME_LIST.indexOf(CLASS_NAME)))).keySet());

                    }else{

                        classExecuteDoneResult.put(jarFile, new ArrayList<>(classInfoList.stream().collect(Collectors.groupingBy(list -> list.get(OUTPUT_HEADER_COMMON_COLUMN_NAME_LIST.indexOf(CLASS_NAME)))).keySet()));

                    }

                }catch (Throwable e){

                    String className = e.getMessage().replace('/','.');
                    String exceptionClassName = e.getClass().getName();

                    classExecuteSkipList.add(className);

                    if(classExecuteCauseDetailInfo.containsKey(exceptionClassName)){

                        classExecuteCauseDetailInfo.get(exceptionClassName).add(className);

                    }else{

                        classExecuteCauseDetailInfo.put(exceptionClassName,new LinkedHashSet<>(Arrays.asList(className)));

                    }
                }
            }

            classExecuteSkipCauseSummaryInfo.put(jarFile,classExecuteCauseDetailInfo);

        }

        System.err.printf(
                "\n" +
                "%s\t%s\n" +
                "%s\t%s\n" +
                "%s\t%s\n" +
                "%s\t%s\n" +
                "%s\t%s\n" +
                "%s\t%s\n" +
                "\n"
                ,"jarFileListCnt",jarFileListCnt
                ,"jarFileClassCnt",jarFileClassCnt
                ,"classLoadListCnt",classLoadList.size()
                ,"classLoadSkipListCnt",classLoadSkipList.size()
                ,"classExecuteListCnt",classExecuteList.size()
                ,"classExecuteSkipListCnt",classExecuteSkipList.size()
        );

        System.err.printf(
                "%s\t%s\n" +
                "%s\t%s\n" +
                "%s\t%s\n" +
                "%s\t%s\n" +
                "\n"
                ,"classLoadList",classLoadList
                ,"classLoadSkipList",classLoadSkipList
                ,"classExecuteList",classExecuteList
                ,"classExecuteSkipList",classExecuteSkipList
        );

        System.err.printf(
                "%s\t%s\n" +
                        "%s\t%s\n" +
                        "%s\t%s\n" +
                        "%s\t%s\n" +
                        "\n"
                ,"classLoadUniqueList",classLoadList.stream().collect(Collectors.toSet())
                ,"classLoadSkipUniqueList",classLoadSkipList.stream().collect(Collectors.toSet())
                ,"classExecuteUniqueList",classExecuteList.stream().collect(Collectors.toSet())
                ,"classExecuteSkipUniqueList",classExecuteSkipList.stream().collect(Collectors.toSet())
        );

        System.err.printf(
                "%s\t%s\n" +
                        "%s\t%s\n" +
                        "%s\t%s\n" +
                        "%s\t%s\n" +
                        "\n"
                ,"classLoadUniqueListCnt",classLoadList.stream().collect(Collectors.toSet()).size()
                ,"classLoadSkipUniqueListCnt",classLoadSkipList.stream().collect(Collectors.toSet()).size()
                ,"classExecuteUniqueListCnt",classExecuteList.stream().collect(Collectors.toSet()).size()
                ,"classExecuteSkipUniqueListCnt",classExecuteSkipList.stream().collect(Collectors.toSet()).size()
        );

        System.err.printf(
                "%s\t%s\n" +
                "%s\t%s\n" +
                "%s\t%s\n" +
                "%s\t%s\n" +
                "\n"
                ,"classLoadDoneResult",classLoadDoneResult
                ,"classLoadSkipResult",classLoadSkipResult
                ,"classExecuteDoneResult",classExecuteDoneResult
                ,"classExecuteSkipResult",classExecuteSkipResult
        );

        System.err.printf(
                "%s\t%s\n" +
                "%s\t%s\n" +
                "\n"
                ,"classLoadSkipCauseSummaryInfo",classLoadSkipCauseSummaryInfo
                ,"classExecuteSkipCauseSummaryInfo",classExecuteSkipCauseSummaryInfo
        );
    }
}