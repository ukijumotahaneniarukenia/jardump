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
