package app;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;

public class AppTest {


//        // 複数件の件数チェック
//        cmdLineArgs = Arrays.asList(
//                USER_HOME_DIR +"/.m2/repository/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar"
//                ,USER_HOME_DIR + "/.m2/repository/org/apache/commons/commons-lang3/3.11/commons-lang3-3.11.jar"
//                ,"--method"
//        );
//
//        // 単一件の件数チェック
//        cmdLineArgs = Arrays.asList(
//                USER_HOME_DIR +"/.m2/repository/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar"
//                ,"--method"
//        );
//
//        // 単一件の件数チェック
//        cmdLineArgs = Arrays.asList(
//                USER_HOME_DIR + "/.m2/repository/org/apache/commons/commons-lang3/3.11/commons-lang3-3.11.jar"
//                ,"--method"
//        );

//        // 複数件の件数チェック
//        cmdLineArgs = Arrays.asList(
//                USER_HOME_DIR +"/.m2/repository/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar"
//                ,USER_HOME_DIR + "/.m2/repository/org/apache/commons/commons-lang3/3.11/commons-lang3-3.11.jar"
//                ,"--constant"
//        );

//        // 単一件の件数チェック
//        cmdLineArgs = Arrays.asList(
//                USER_HOME_DIR +"/.m2/repository/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar"
//                ,"--constant"
//        );

//        // 単一件の件数チェック
//        cmdLineArgs = Arrays.asList(
//                USER_HOME_DIR + "/.m2/repository/org/apache/commons/commons-lang3/3.11/commons-lang3-3.11.jar"
//                ,"--constant"
//        );

    // 異常系もりもりチェック このjarFileは件数合わない なぞ
//        cmdLineArgs = Arrays.asList(
//                USER_HOME_DIR +"/scala-xml_2.13-1.3.0.jar"
//                ,"--method"
//        );

    // 異常系もりもりチェック このjarFileは件数合わない なぞ
//        cmdLineArgs = Arrays.asList(
//                USER_HOME_DIR +"/scala-xml_2.13-1.3.0.jar"
//                ,"--constant"
//        );

    // 異常系もりもりチェック このjarFileは件数合う
//        cmdLineArgs = Arrays.asList(
//                USER_HOME_DIR +"/.m2/repository/org/apache/httpcomponents/httpclient/4.3.5/httpclient-4.3.5.jar"
//                ,"--method"
//        );

    // 異常系もりもりチェック このjarFileは件数合う
//    cmdLineArgs = Arrays.asList(
//    USER_HOME_DIR +"/.m2/repository/org/apache/httpcomponents/httpclient/4.3.5/httpclient-4.3.5.jar"
//            ,"--constant"
//            );
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
}
