# jardump
javalangのjarファイル内に存在するクラス定義情報をクロステーブル形式で標準出力に出力するコマンド

# ダウンロード

[jardump](https://github.com/ukijumotahaneniarukenia/jardump/releases/tag/3-0-0)

```
curl -fsSLO https://github.com/ukijumotahaneniarukenia/jardump/releases/download/3-0-0/jardump-3-0-0-SNAPSHOT.jar
```

# 実行

```
#jarファイル未指定（mavenのデフォルトのレポジトリ配下すべてのjarファイルが対象）
time java -jar jardump-3-0-0-SNAPSHOT.jar

#単一jarファイル指定
time java -jar jardump-3-0-0-SNAPSHOT.jar /home/kuraine/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar

#複数jarファイル指定
time java -jar jardump-3-0-0-SNAPSHOT.jar /home/kuraine/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar /home/kuraine/.m2/repository/org/ow2/asm/asm-tree/4.1/asm-tree-4.1.jar
```
