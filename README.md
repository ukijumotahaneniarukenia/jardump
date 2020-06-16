# jardump
javalangのjarファイル内に存在するクラス定義情報をクロステーブル形式で標準出力に出力するコマンド

# ダウンロード

[jardump](https://github.com/ukijumotahaneniarukenia/jardump/releases/tag/3-0-0)

```
curl -fsSLO https://github.com/ukijumotahaneniarukenia/jardump/releases/download/3-0-0/jardump-3-0-0-SNAPSHOT.jar
```

# 実行

各種ライブラリ管理ディレクトリ配下すべてのjarないしはコマンドライン引数に指定したjarファイルを対象にクラス定義情報をクロステーブル形式で標準出力に出力する

```
Usageだよーん
java -jar jardump-4-0-0-SNAPSHOT.jar --maven
or
java -jar jardump-4-0-0-SNAPSHOT.jar --gradle
or
java -jar jardump-4-0-0-SNAPSHOT.jar --kotlin
or
java -jar jardump-4-0-0-SNAPSHOT.jar --scala
or
java -jar jardump-4-0-0-SNAPSHOT.jar /home/kuraine/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup-1.10.2.jar /home/kuraine/.sdkman/candidates/scala/current/lib/jline-3.14.1.jar
or
java -jar jardump-4-0-0-SNAPSHOT.jar /home/kuraine/.sdkman/candidates/kotlin/current/lib/kotlin-stdlib.jar /home/kuraine/.sdkman/candidates/scala/current/lib/scala-library.jar
```
