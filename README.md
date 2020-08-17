# jardump
javalangのjarファイル内に存在するクラス定義情報をクロステーブル形式で標準出力に出力するコマンド

# ダウンロード

[jardump](https://github.com/ukijumotahaneniarukenia/jardump/releases)

```
curl -fsSLO https://github.com/ukijumotahaneniarukenia/jardump/releases/download/X-X-X/jardump-X.X.X-SNAPSHOT-jar-with-dependencies.jar
```

# 実行

各種ライブラリ管理ディレクトリ配下すべてのjarないしはコマンドライン引数に指定したjarファイルを対象にクラス定義情報をクロステーブル形式で標準出力に出力する

デフォルトで標準エラー出力にクラスロード時ないしは実行時に依存関係が解決できず、スキップしたクラスリストなどを出力している

スキップしたもののみを出力したいときは 1>/dev/nullをクロステーブルの一覧のみを出力したい場合は 2>/dev/nullを指定し、よしなにするなど

```
Usageだよーん
java -jar jardump-X-X-X-SNAPSHOT.jar --maven
or
java -jar jardump-X-X-X-SNAPSHOT.jar --gradle
or
java -jar jardump-X-X-X-SNAPSHOT.jar --kotlin
or
java -jar jardump-X-X-X-SNAPSHOT.jar --scala
or
java -jar jardump-X-X-X-SNAPSHOT.jar --scala --method
or
java -jar jardump-X-X-X-SNAPSHOT.jar --scala --constant
or
java -jar jardump-X-X-X-SNAPSHOT.jar /home/aine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup-1.10.2.jar /home/aine/.m2/repository/org/apache/commons/commons-lang3/3.11/commons-lang3-3.11.jar
or
java -jar jardump-X-X-X-SNAPSHOT.jar /home/aine/.m2/repository/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar


```


番外編

```
java -jar jardump-4.2.0-SNAPSHOT-jar-with-dependencies.jar scala-xml_2.13-1.3.0.jar --method 2>/dev/null | awk -v FS="\t" '$7=="java.lang.String"&&$6=="public"{print $5,$6,$7,$8}' OFS="\t"
```

いけてるjarFileかどうか判定

```
$ java -jar jardump-4.2.0-SNAPSHOT-jar-with-dependencies.jar json-20200518.jar |& awk -v FS="\t" '$1 ~ /.*Cnt/{print $1,$2}' OFS="\t"
jarFileListCnt	1
jarFileClassCnt	26
classLoadListCnt	26
classLoadSkipListCnt	0
classExecuteListCnt	26
classExecuteSkipListCnt	0
classLoadUniqueListCnt	26
classLoadSkipUniqueListCnt	0
classExecuteUniqueListCnt	26
classExecuteSkipUniqueListCnt	0
```

例えば、以下の２つのjarFileの出力結果を見比べてみる

スキップ件数がクラス定義ないなどの某かの理由でローディングできなかった場合があったことを示している

```
$ java -jar jardump-4.2.0-SNAPSHOT-jar-with-dependencies.jar guava-29.0-jre.jar |& awk -v FS="\t" '$1 ~ /.*Cnt/{print $1,$2}' OFS="\t"
jarFileListCnt	1
jarFileClassCnt	1975
classLoadListCnt	1950
classLoadSkipListCnt	25
classExecuteListCnt	1934
classExecuteSkipListCnt	1
classLoadUniqueListCnt	1950
classLoadSkipUniqueListCnt	25
classExecuteUniqueListCnt	1934
classExecuteSkipUniqueListCnt	1
```

一方、こちらのjarファイルはスキップ件数がゼロ件

jarに梱包されているクラスファイルはすべてJVMにローディング完了し、クラス定義を引っ張り出す実行時にもエラー等は特にでなかった。

```
$ java -jar jardump-4.2.0-SNAPSHOT-jar-with-dependencies.jar jsoup-1.13.1.jar |& awk -v FS="\t" '$1 ~ /.*Cnt/{print $1,$2}' OFS="\t"
jarFileListCnt	1
jarFileClassCnt	244
classLoadListCnt	244
classLoadSkipListCnt	0
classExecuteListCnt	244
classExecuteSkipListCnt	0
classLoadUniqueListCnt	244
classLoadSkipUniqueListCnt	0
classExecuteUniqueListCnt	244
classExecuteSkipUniqueListCnt	0
```

jdk梱包のjdepsコマンドでもういちど観察してみる

おおくのライブラリに依存していることがわかる

ファイルサイズもでかい

```
$ jdeps guava-29.0-jre.jar 
guava-29.0-jre.jar -> java.base
guava-29.0-jre.jar -> java.logging
guava-29.0-jre.jar -> jdk.unsupported
guava-29.0-jre.jar -> 見つかりません
   com.google.common.annotations                      -> java.lang                                          java.base
   com.google.common.annotations                      -> java.lang.annotation                               java.base
   com.google.common.base                             -> com.google.errorprone.annotations                  見つかりません
   com.google.common.base                             -> com.google.errorprone.annotations.concurrent       見つかりません
   com.google.common.base                             -> java.io                                            java.base
   com.google.common.base                             -> java.lang                                          java.base
   com.google.common.base                             -> java.lang.ref                                      java.base
   com.google.common.base                             -> java.lang.reflect                                  java.base
   com.google.common.base                             -> java.net                                           java.base
   com.google.common.base                             -> java.nio.charset                                   java.base
   com.google.common.base                             -> java.time                                          java.base
   com.google.common.base                             -> java.util                                          java.base
   com.google.common.base                             -> java.util.concurrent                               java.base
   com.google.common.base                             -> java.util.function                                 java.base
   com.google.common.base                             -> java.util.logging                                  java.logging
   com.google.common.base                             -> java.util.regex                                    java.base
   com.google.common.base                             -> java.util.stream                                   java.base
   com.google.common.base                             -> javax.annotation                                   見つかりません
   com.google.common.base.internal                    -> java.lang                                          java.base
   com.google.common.base.internal                    -> java.lang.ref                                      java.base
   com.google.common.base.internal                    -> java.lang.reflect                                  java.base
   com.google.common.base.internal                    -> java.util.logging                                  java.logging
   com.google.common.cache                            -> com.google.common.base                             guava-29.0-jre.jar
   com.google.common.cache                            -> com.google.common.collect                          guava-29.0-jre.jar
   com.google.common.cache                            -> com.google.common.math                             guava-29.0-jre.jar
   com.google.common.cache                            -> com.google.common.primitives                       guava-29.0-jre.jar
   com.google.common.cache                            -> com.google.common.util.concurrent                  guava-29.0-jre.jar
   com.google.common.cache                            -> com.google.errorprone.annotations                  見つかりません
   com.google.common.cache                            -> java.io                                            java.base
   com.google.common.cache                            -> java.lang                                          java.base
   com.google.common.cache                            -> java.lang.invoke                                   java.base
   com.google.common.cache                            -> java.lang.ref                                      java.base
   com.google.common.cache                            -> java.lang.reflect                                  java.base
   com.google.common.cache                            -> java.security                                      java.base
   com.google.common.cache                            -> java.time                                          java.base
   com.google.common.cache                            -> java.util                                          java.base
   com.google.common.cache                            -> java.util.concurrent                               java.base
   com.google.common.cache                            -> java.util.concurrent.atomic                        java.base
   com.google.common.cache                            -> java.util.concurrent.locks                         java.base
   com.google.common.cache                            -> java.util.function                                 java.base
   com.google.common.cache                            -> java.util.logging                                  java.logging
   com.google.common.cache                            -> javax.annotation                                   見つかりません
   com.google.common.cache                            -> sun.misc                                           JDK internal API (jdk.unsupported)
   com.google.common.collect                          -> com.google.common.base                             guava-29.0-jre.jar
   com.google.common.collect                          -> com.google.common.math                             guava-29.0-jre.jar
   com.google.common.collect                          -> com.google.common.primitives                       guava-29.0-jre.jar
   com.google.common.collect                          -> com.google.errorprone.annotations                  見つかりません
   com.google.common.collect                          -> com.google.errorprone.annotations.concurrent       見つかりません
   com.google.common.collect                          -> java.io                                            java.base
   com.google.common.collect                          -> java.lang                                          java.base
   com.google.common.collect                          -> java.lang.annotation                               java.base
   com.google.common.collect                          -> java.lang.invoke                                   java.base
   com.google.common.collect                          -> java.lang.ref                                      java.base
   com.google.common.collect                          -> java.lang.reflect                                  java.base
   com.google.common.collect                          -> java.math                                          java.base
   com.google.common.collect                          -> java.time                                          java.base
   com.google.common.collect                          -> java.util                                          java.base
   com.google.common.collect                          -> java.util.concurrent                               java.base
   com.google.common.collect                          -> java.util.concurrent.atomic                        java.base
   com.google.common.collect                          -> java.util.concurrent.locks                         java.base
   com.google.common.collect                          -> java.util.function                                 java.base
   com.google.common.collect                          -> java.util.logging                                  java.logging
   com.google.common.collect                          -> java.util.stream                                   java.base
   com.google.common.collect                          -> javax.annotation                                   見つかりません
   com.google.common.escape                           -> com.google.common.base                             guava-29.0-jre.jar
   com.google.common.escape                           -> com.google.errorprone.annotations                  見つかりません
   com.google.common.escape                           -> java.lang                                          java.base
   com.google.common.escape                           -> java.util                                          java.base
   com.google.common.escape                           -> javax.annotation                                   見つかりません
   com.google.common.eventbus                         -> com.google.common.base                             guava-29.0-jre.jar
   com.google.common.eventbus                         -> com.google.common.cache                            guava-29.0-jre.jar
   com.google.common.eventbus                         -> com.google.common.collect                          guava-29.0-jre.jar
   com.google.common.eventbus                         -> com.google.common.reflect                          guava-29.0-jre.jar
   com.google.common.eventbus                         -> com.google.common.util.concurrent                  guava-29.0-jre.jar
   com.google.common.eventbus                         -> com.google.errorprone.annotations                  見つかりません
   com.google.common.eventbus                         -> java.lang                                          java.base
   com.google.common.eventbus                         -> java.lang.annotation                               java.base
   com.google.common.eventbus                         -> java.lang.reflect                                  java.base
   com.google.common.eventbus                         -> java.util                                          java.base
   com.google.common.eventbus                         -> java.util.concurrent                               java.base
   com.google.common.eventbus                         -> java.util.logging                                  java.logging
   com.google.common.eventbus                         -> javax.annotation                                   見つかりません
   com.google.common.graph                            -> com.google.common.base                             guava-29.0-jre.jar
   com.google.common.graph                            -> com.google.common.collect                          guava-29.0-jre.jar
   com.google.common.graph                            -> com.google.common.math                             guava-29.0-jre.jar
   com.google.common.graph                            -> com.google.common.primitives                       guava-29.0-jre.jar
   com.google.common.graph                            -> com.google.errorprone.annotations                  見つかりません
   com.google.common.graph                            -> com.google.errorprone.annotations.concurrent       見つかりません
   com.google.common.graph                            -> java.lang                                          java.base
   com.google.common.graph                            -> java.lang.ref                                      java.base
   com.google.common.graph                            -> java.util                                          java.base
   com.google.common.graph                            -> java.util.concurrent.atomic                        java.base
   com.google.common.graph                            -> javax.annotation                                   見つかりません
   com.google.common.hash                             -> com.google.common.base                             guava-29.0-jre.jar
   com.google.common.hash                             -> com.google.common.math                             guava-29.0-jre.jar
   com.google.common.hash                             -> com.google.common.primitives                       guava-29.0-jre.jar
   com.google.common.hash                             -> com.google.errorprone.annotations                  見つかりません
   com.google.common.hash                             -> java.io                                            java.base
   com.google.common.hash                             -> java.lang                                          java.base
   com.google.common.hash                             -> java.lang.invoke                                   java.base
   com.google.common.hash                             -> java.lang.reflect                                  java.base
   com.google.common.hash                             -> java.math                                          java.base
   com.google.common.hash                             -> java.nio                                           java.base
   com.google.common.hash                             -> java.nio.charset                                   java.base
   com.google.common.hash                             -> java.security                                      java.base
   com.google.common.hash                             -> java.util                                          java.base
   com.google.common.hash                             -> java.util.concurrent.atomic                        java.base
   com.google.common.hash                             -> java.util.function                                 java.base
   com.google.common.hash                             -> java.util.stream                                   java.base
   com.google.common.hash                             -> java.util.zip                                      java.base
   com.google.common.hash                             -> javax.annotation                                   見つかりません
   com.google.common.hash                             -> javax.crypto                                       java.base
   com.google.common.hash                             -> javax.crypto.spec                                  java.base
   com.google.common.hash                             -> sun.misc                                           JDK internal API (jdk.unsupported)
   com.google.common.html                             -> com.google.common.escape                           guava-29.0-jre.jar
   com.google.common.html                             -> com.google.errorprone.annotations                  見つかりません
   com.google.common.html                             -> java.lang                                          java.base
   com.google.common.html                             -> javax.annotation                                   見つかりません
   com.google.common.io                               -> com.google.common.base                             guava-29.0-jre.jar
   com.google.common.io                               -> com.google.common.collect                          guava-29.0-jre.jar
   com.google.common.io                               -> com.google.common.graph                            guava-29.0-jre.jar
   com.google.common.io                               -> com.google.common.hash                             guava-29.0-jre.jar
   com.google.common.io                               -> com.google.common.math                             guava-29.0-jre.jar
   com.google.common.io                               -> com.google.common.primitives                       guava-29.0-jre.jar
   com.google.common.io                               -> com.google.errorprone.annotations                  見つかりません
   com.google.common.io                               -> com.google.errorprone.annotations.concurrent       見つかりません
   com.google.common.io                               -> java.io                                            java.base
   com.google.common.io                               -> java.lang                                          java.base
   com.google.common.io                               -> java.lang.invoke                                   java.base
   com.google.common.io                               -> java.lang.reflect                                  java.base
   com.google.common.io                               -> java.math                                          java.base
   com.google.common.io                               -> java.net                                           java.base
   com.google.common.io                               -> java.nio                                           java.base
   com.google.common.io                               -> java.nio.channels                                  java.base
   com.google.common.io                               -> java.nio.charset                                   java.base
   com.google.common.io                               -> java.nio.file                                      java.base
   com.google.common.io                               -> java.nio.file.attribute                            java.base
   com.google.common.io                               -> java.util                                          java.base
   com.google.common.io                               -> java.util.function                                 java.base
   com.google.common.io                               -> java.util.logging                                  java.logging
   com.google.common.io                               -> java.util.regex                                    java.base
   com.google.common.io                               -> java.util.stream                                   java.base
   com.google.common.io                               -> javax.annotation                                   見つかりません
   com.google.common.math                             -> com.google.common.base                             guava-29.0-jre.jar
   com.google.common.math                             -> com.google.common.primitives                       guava-29.0-jre.jar
   com.google.common.math                             -> com.google.errorprone.annotations                  見つかりません
   com.google.common.math                             -> com.google.errorprone.annotations.concurrent       見つかりません
   com.google.common.math                             -> java.io                                            java.base
   com.google.common.math                             -> java.lang                                          java.base
   com.google.common.math                             -> java.lang.invoke                                   java.base
   com.google.common.math                             -> java.math                                          java.base
   com.google.common.math                             -> java.nio                                           java.base
   com.google.common.math                             -> java.util                                          java.base
   com.google.common.math                             -> java.util.function                                 java.base
   com.google.common.math                             -> java.util.stream                                   java.base
   com.google.common.math                             -> javax.annotation                                   見つかりません
   com.google.common.net                              -> com.google.common.base                             guava-29.0-jre.jar
   com.google.common.net                              -> com.google.common.collect                          guava-29.0-jre.jar
   com.google.common.net                              -> com.google.common.escape                           guava-29.0-jre.jar
   com.google.common.net                              -> com.google.common.hash                             guava-29.0-jre.jar
   com.google.common.net                              -> com.google.common.io                               guava-29.0-jre.jar
   com.google.common.net                              -> com.google.common.primitives                       guava-29.0-jre.jar
   com.google.common.net                              -> com.google.errorprone.annotations                  見つかりません
   com.google.common.net                              -> com.google.errorprone.annotations.concurrent       見つかりません
   com.google.common.net                              -> com.google.thirdparty.publicsuffix                 guava-29.0-jre.jar
   com.google.common.net                              -> java.io                                            java.base
   com.google.common.net                              -> java.lang                                          java.base
   com.google.common.net                              -> java.math                                          java.base
   com.google.common.net                              -> java.net                                           java.base
   com.google.common.net                              -> java.nio                                           java.base
   com.google.common.net                              -> java.nio.charset                                   java.base
   com.google.common.net                              -> java.text                                          java.base
   com.google.common.net                              -> java.util                                          java.base
   com.google.common.net                              -> javax.annotation                                   見つかりません
   com.google.common.primitives                       -> com.google.common.base                             guava-29.0-jre.jar
   com.google.common.primitives                       -> com.google.errorprone.annotations                  見つかりません
   com.google.common.primitives                       -> java.io                                            java.base
   com.google.common.primitives                       -> java.lang                                          java.base
   com.google.common.primitives                       -> java.lang.invoke                                   java.base
   com.google.common.primitives                       -> java.lang.reflect                                  java.base
   com.google.common.primitives                       -> java.math                                          java.base
   com.google.common.primitives                       -> java.nio                                           java.base
   com.google.common.primitives                       -> java.security                                      java.base
   com.google.common.primitives                       -> java.util                                          java.base
   com.google.common.primitives                       -> java.util.function                                 java.base
   com.google.common.primitives                       -> java.util.logging                                  java.logging
   com.google.common.primitives                       -> java.util.regex                                    java.base
   com.google.common.primitives                       -> java.util.stream                                   java.base
   com.google.common.primitives                       -> javax.annotation                                   見つかりません
   com.google.common.primitives                       -> sun.misc                                           JDK internal API (jdk.unsupported)
   com.google.common.reflect                          -> com.google.common.base                             guava-29.0-jre.jar
   com.google.common.reflect                          -> com.google.common.collect                          guava-29.0-jre.jar
   com.google.common.reflect                          -> com.google.common.io                               guava-29.0-jre.jar
   com.google.common.reflect                          -> com.google.common.primitives                       guava-29.0-jre.jar
   com.google.common.reflect                          -> com.google.errorprone.annotations                  見つかりません
   com.google.common.reflect                          -> java.io                                            java.base
   com.google.common.reflect                          -> java.lang                                          java.base
   com.google.common.reflect                          -> java.lang.annotation                               java.base
   com.google.common.reflect                          -> java.lang.reflect                                  java.base
   com.google.common.reflect                          -> java.net                                           java.base
   com.google.common.reflect                          -> java.nio.charset                                   java.base
   com.google.common.reflect                          -> java.security                                      java.base
   com.google.common.reflect                          -> java.util                                          java.base
   com.google.common.reflect                          -> java.util.concurrent.atomic                        java.base
   com.google.common.reflect                          -> java.util.jar                                      java.base
   com.google.common.reflect                          -> java.util.logging                                  java.logging
   com.google.common.reflect                          -> javax.annotation                                   見つかりません
   com.google.common.util.concurrent                  -> com.google.common.base                             guava-29.0-jre.jar
   com.google.common.util.concurrent                  -> com.google.common.collect                          guava-29.0-jre.jar
   com.google.common.util.concurrent                  -> com.google.common.math                             guava-29.0-jre.jar
   com.google.common.util.concurrent                  -> com.google.common.primitives                       guava-29.0-jre.jar
   com.google.common.util.concurrent                  -> com.google.common.util.concurrent.internal         見つかりません
   com.google.common.util.concurrent                  -> com.google.errorprone.annotations                  見つかりません
   com.google.common.util.concurrent                  -> com.google.j2objc.annotations                      見つかりません
   com.google.common.util.concurrent                  -> java.io                                            java.base
   com.google.common.util.concurrent                  -> java.lang                                          java.base
   com.google.common.util.concurrent                  -> java.lang.annotation                               java.base
   com.google.common.util.concurrent                  -> java.lang.invoke                                   java.base
   com.google.common.util.concurrent                  -> java.lang.ref                                      java.base
   com.google.common.util.concurrent                  -> java.lang.reflect                                  java.base
   com.google.common.util.concurrent                  -> java.math                                          java.base
   com.google.common.util.concurrent                  -> java.security                                      java.base
   com.google.common.util.concurrent                  -> java.time                                          java.base
   com.google.common.util.concurrent                  -> java.util                                          java.base
   com.google.common.util.concurrent                  -> java.util.concurrent                               java.base
   com.google.common.util.concurrent                  -> java.util.concurrent.atomic                        java.base
   com.google.common.util.concurrent                  -> java.util.concurrent.locks                         java.base
   com.google.common.util.concurrent                  -> java.util.function                                 java.base
   com.google.common.util.concurrent                  -> java.util.logging                                  java.logging
   com.google.common.util.concurrent                  -> java.util.stream                                   java.base
   com.google.common.util.concurrent                  -> javax.annotation                                   見つかりません
   com.google.common.util.concurrent                  -> sun.misc                                           JDK internal API (jdk.unsupported)
   com.google.common.xml                              -> com.google.common.escape                           guava-29.0-jre.jar
   com.google.common.xml                              -> com.google.errorprone.annotations                  見つかりません
   com.google.common.xml                              -> java.lang                                          java.base
   com.google.common.xml                              -> javax.annotation                                   見つかりません
   com.google.thirdparty.publicsuffix                 -> com.google.common.base                             guava-29.0-jre.jar
   com.google.thirdparty.publicsuffix                 -> com.google.common.collect                          guava-29.0-jre.jar
   com.google.thirdparty.publicsuffix                 -> java.lang                                          java.base
   com.google.thirdparty.publicsuffix                 -> java.util                                          java.base
```

jdk標準のライブラリないしはそれ自身で収まっていることがわかる

```
$ jdeps jsoup-1.13.1.jar 
jsoup-1.13.1.jar -> java.base
jsoup-1.13.1.jar -> java.xml
   org.jsoup                                          -> java.io                                            java.base
   org.jsoup                                          -> java.lang                                          java.base
   org.jsoup                                          -> java.net                                           java.base
   org.jsoup                                          -> java.util                                          java.base
   org.jsoup                                          -> javax.net.ssl                                      java.base
   org.jsoup                                          -> org.jsoup.helper                                   jsoup-1.13.1.jar
   org.jsoup                                          -> org.jsoup.nodes                                    jsoup-1.13.1.jar
   org.jsoup                                          -> org.jsoup.parser                                   jsoup-1.13.1.jar
   org.jsoup                                          -> org.jsoup.safety                                   jsoup-1.13.1.jar
   org.jsoup.helper                                   -> java.io                                            java.base
   org.jsoup.helper                                   -> java.lang                                          java.base
   org.jsoup.helper                                   -> java.net                                           java.base
   org.jsoup.helper                                   -> java.nio                                           java.base
   org.jsoup.helper                                   -> java.nio.charset                                   java.base
   org.jsoup.helper                                   -> java.util                                          java.base
   org.jsoup.helper                                   -> java.util.regex                                    java.base
   org.jsoup.helper                                   -> java.util.zip                                      java.base
   org.jsoup.helper                                   -> javax.net.ssl                                      java.base
   org.jsoup.helper                                   -> javax.xml.parsers                                  java.xml
   org.jsoup.helper                                   -> javax.xml.transform                                java.xml
   org.jsoup.helper                                   -> javax.xml.transform.dom                            java.xml
   org.jsoup.helper                                   -> javax.xml.transform.stream                         java.xml
   org.jsoup.helper                                   -> org.jsoup                                          jsoup-1.13.1.jar
   org.jsoup.helper                                   -> org.jsoup.internal                                 jsoup-1.13.1.jar
   org.jsoup.helper                                   -> org.jsoup.nodes                                    jsoup-1.13.1.jar
   org.jsoup.helper                                   -> org.jsoup.parser                                   jsoup-1.13.1.jar
   org.jsoup.helper                                   -> org.jsoup.select                                   jsoup-1.13.1.jar
   org.jsoup.helper                                   -> org.w3c.dom                                        java.xml
   org.jsoup.internal                                 -> java.io                                            java.base
   org.jsoup.internal                                 -> java.lang                                          java.base
   org.jsoup.internal                                 -> java.net                                           java.base
   org.jsoup.internal                                 -> java.nio                                           java.base
   org.jsoup.internal                                 -> java.util                                          java.base
   org.jsoup.internal                                 -> org.jsoup.helper                                   jsoup-1.13.1.jar
   org.jsoup.nodes                                    -> java.io                                            java.base
   org.jsoup.nodes                                    -> java.lang                                          java.base
   org.jsoup.nodes                                    -> java.lang.ref                                      java.base
   org.jsoup.nodes                                    -> java.nio.charset                                   java.base
   org.jsoup.nodes                                    -> java.util                                          java.base
   org.jsoup.nodes                                    -> java.util.regex                                    java.base
   org.jsoup.nodes                                    -> org.jsoup                                          jsoup-1.13.1.jar
   org.jsoup.nodes                                    -> org.jsoup.helper                                   jsoup-1.13.1.jar
   org.jsoup.nodes                                    -> org.jsoup.internal                                 jsoup-1.13.1.jar
   org.jsoup.nodes                                    -> org.jsoup.parser                                   jsoup-1.13.1.jar
   org.jsoup.nodes                                    -> org.jsoup.select                                   jsoup-1.13.1.jar
   org.jsoup.parser                                   -> java.io                                            java.base
   org.jsoup.parser                                   -> java.lang                                          java.base
   org.jsoup.parser                                   -> java.util                                          java.base
   org.jsoup.parser                                   -> org.jsoup                                          jsoup-1.13.1.jar
   org.jsoup.parser                                   -> org.jsoup.helper                                   jsoup-1.13.1.jar
   org.jsoup.parser                                   -> org.jsoup.internal                                 jsoup-1.13.1.jar
   org.jsoup.parser                                   -> org.jsoup.nodes                                    jsoup-1.13.1.jar
   org.jsoup.parser                                   -> org.jsoup.select                                   jsoup-1.13.1.jar
   org.jsoup.safety                                   -> java.lang                                          java.base
   org.jsoup.safety                                   -> java.util                                          java.base
   org.jsoup.safety                                   -> org.jsoup.helper                                   jsoup-1.13.1.jar
   org.jsoup.safety                                   -> org.jsoup.internal                                 jsoup-1.13.1.jar
   org.jsoup.safety                                   -> org.jsoup.nodes                                    jsoup-1.13.1.jar
   org.jsoup.safety                                   -> org.jsoup.parser                                   jsoup-1.13.1.jar
   org.jsoup.safety                                   -> org.jsoup.select                                   jsoup-1.13.1.jar
   org.jsoup.select                                   -> java.lang                                          java.base
   org.jsoup.select                                   -> java.util                                          java.base
   org.jsoup.select                                   -> java.util.regex                                    java.base
   org.jsoup.select                                   -> org.jsoup.helper                                   jsoup-1.13.1.jar
   org.jsoup.select                                   -> org.jsoup.internal                                 jsoup-1.13.1.jar
   org.jsoup.select                                   -> org.jsoup.nodes                                    jsoup-1.13.1.jar
   org.jsoup.select                                   -> org.jsoup.parser                                   jsoup-1.13.1.jar
```

よくわからんが、このjardumpは依存関係の調査にもいいかんじに使えるのかもしれない

jdepsコマンド便利


同一クラス内の定数とメソッドを出力する例

```
$ java -jar jardump-4.2.0-SNAPSHOT-jar-with-dependencies.jar htmlcleaner-2.24.jar --method 2>/dev/null | grep 00000008
htmlcleaner-2.24.jar	MMMMM	00000008	0001	org.htmlcleaner.CData	public	boolean	isBlank	false	0				
htmlcleaner-2.24.jar	MMMMM	00000008	0002	org.htmlcleaner.CData	public	org.htmlcleaner.TagNode	getParent	false	0				
htmlcleaner-2.24.jar	MMMMM	00000008	0003	org.htmlcleaner.CData	public	java.lang.String	toString	false	0				
htmlcleaner-2.24.jar	MMMMM	00000008	0004	org.htmlcleaner.CData	public	java.lang.String	getContentWithoutStartAndEndTokens	false	0				
htmlcleaner-2.24.jar	MMMMM	00000008	0005	org.htmlcleaner.CData	public	int	getRow	false	0				
htmlcleaner-2.24.jar	MMMMM	00000008	0006	org.htmlcleaner.CData	public	int	getCol	false	0				
htmlcleaner-2.24.jar	MMMMM	00000008	0007	org.htmlcleaner.CData	public	java.util.List<? extends org.htmlcleaner.BaseToken>	getSiblings	false	0			
htmlcleaner-2.24.jar	MMMMM	00000008	0008	org.htmlcleaner.CData	public native	int	hashCode	false	0				
htmlcleaner-2.24.jar	MMMMM	00000008	0009	org.htmlcleaner.CData	public	boolean	equals	false	1			java.lang.Object	arg0
htmlcleaner-2.24.jar	MMMMM	00000008	0010	org.htmlcleaner.CData	public	void	setRow	false	1			int	arg0
htmlcleaner-2.24.jar	MMMMM	00000008	0011	org.htmlcleaner.CData	public final native	void	notify	false	0				
htmlcleaner-2.24.jar	MMMMM	00000008	0012	org.htmlcleaner.CData	public	java.lang.String	getContentWithStartAndEndTokens	false	0				
htmlcleaner-2.24.jar	MMMMM	00000008	0013	org.htmlcleaner.CData	public	void	setCol	false	1			int	arg0
htmlcleaner-2.24.jar	MMMMM	00000008	0014	org.htmlcleaner.CData	public	java.lang.String	getContent	false	0				
htmlcleaner-2.24.jar	MMMMM	00000008	0015	org.htmlcleaner.CData	public final native	void	wait	false	1			long	arg0
htmlcleaner-2.24.jar	MMMMM	00000008	0016	org.htmlcleaner.CData	public final	void	wait	false	2			long,int	arg0,arg1
htmlcleaner-2.24.jar	MMMMM	00000008	0017	org.htmlcleaner.CData	public final	void	wait	false	0				
htmlcleaner-2.24.jar	MMMMM	00000008	0018	org.htmlcleaner.CData	public	void	serialize	false	2			org.htmlcleaner.Serializer,java.io.Writer	arg0,arg1
htmlcleaner-2.24.jar	MMMMM	00000008	0019	org.htmlcleaner.CData	public final native	java.lang.Class<?>	getClass	false	0				
htmlcleaner-2.24.jar	MMMMM	00000008	0020	org.htmlcleaner.CData	public	void	setParent	false	1			org.htmlcleaner.TagNode	arg0
htmlcleaner-2.24.jar	MMMMM	00000008	0021	org.htmlcleaner.CData	public final native	void	notifyAll	false	0				


$ java -jar jardump-4.2.0-SNAPSHOT-jar-with-dependencies.jar htmlcleaner-2.24.jar --constant 2>/dev/null | grep 00000008
htmlcleaner-2.24.jar	CCCCC	00000008	0001	org.htmlcleaner.CData	SAFE_BEGIN_CDATA_ALT
htmlcleaner-2.24.jar	CCCCC	00000008	0002	org.htmlcleaner.CData	SAFE_END_CDATA_ALT
htmlcleaner-2.24.jar	CCCCC	00000008	0003	org.htmlcleaner.CData	END_CDATA
htmlcleaner-2.24.jar	CCCCC	00000008	0004	org.htmlcleaner.CData	BEGIN_CDATA
htmlcleaner-2.24.jar	CCCCC	00000008	0005	org.htmlcleaner.CData	SAFE_BEGIN_CDATA
htmlcleaner-2.24.jar	CCCCC	00000008	0006	org.htmlcleaner.CData	SAFE_END_CDATA
```

パッケージ名単位に紐づくクラス件数

```
$ java -jar jardump-4.2.0-SNAPSHOT-jar-with-dependencies.jar commons-lang3-3.11.jar --method 2>/dev/null | awk '{print $5}' | uniq | ruby -F'\.' -anle 'p $F[0..$F.size-2].join("-"),$F[$F.size-1]'|xargs -n2 | awk '{a[$1]=a[$1]","$2}END{for(e in a)print e,a[e]}'|sed 's/,/\t/'|ruby -F'\t' -anle 'p $F[0],$F[1].split(",").size'|xargs -n2|grep -v クラス名|sort -nrk2
org-apache-commons-lang3  63
org-apache-commons-lang3-time  60
org-apache-commons-lang3-builder  45
org-apache-commons-lang3-function  44
org-apache-commons-lang3-concurrent  35
org-apache-commons-lang3-text  21
org-apache-commons-lang3-reflect  14
org-apache-commons-lang3-text-translate  13
org-apache-commons-lang3-mutable  9
org-apache-commons-lang3-tuple  8
org-apache-commons-lang3-exception  6
org-apache-commons-lang3-event  4
org-apache-commons-lang3-concurrent-locks  4
org-apache-commons-lang3-compare  4
org-apache-commons-lang3-stream  3
org-apache-commons-lang3-math  3
org-apache-commons-lang3-arch  3
```
