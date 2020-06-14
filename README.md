# jardump
javalangのjarファイル内に存在するクラス定義情報をクロステーブル形式で標準出力に出力するコマンド

# もとねた
script-sketch/java/00059-java-特定のジャーに含まれる定数メソッド一覧取得/

# ダウンロード

[jardump](https://github.com/ukijumotahaneniarukenia/jardump/releases)

```
unizp jardump-3-0-0-SNAPSHOT-bin.zip

or

tar xvf jardump-3-0-0-SNAPSHOT-bin.tar.gz

or

bunzip2 jardump-3-0-0-SNAPSHOT-bin.tar.bz2
```

# 実行

```
#jarファイル未指定（mavenのデフォルトのレポジトリ配下すべてのjarファイルが対象）
time java -jar jardump-2-0-0-SNAPSHOT/jardump-3-0-0-SNAPSHOT.jar

#単一jarファイル指定
time java -jar jardump-2-0-0-SNAPSHOT/jardump-3-0-0-SNAPSHOT.jar /home/kuraine/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar

#複数jarファイル指定
time java -jar jardump-2-0-0-SNAPSHOT/jardump-3-0-0-SNAPSHOT.jar /home/kuraine/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar /home/kuraine/.m2/repository/org/ow2/asm/asm-tree/4.1/asm-tree-4.1.jar
```
