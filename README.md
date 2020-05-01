# jardump
javalangのjarファイル内に存在するクラス定義情報をクロステーブル形式で標準出力に出力するコマンド

# もとねた
script-sketch/java/00059-java-特定のジャーに含まれる定数メソッド一覧取得/

# ダウンロード

[jardump](https://github.com/ukijumotahaneniarukenia/jardump/releases)

```
unizp jardump-1.0-SNAPSHOT-bin.zip

or

tar xvf jardump-1.0-SNAPSHOT-bin.tar.gz

or

tar xvf jardump-1.0-SNAPSHOT-bin.tar.bz2
```

# インストール

```
cat <<EOS >>~/.bashrc
function jardump() {
  JAVA_CMD=$(which java)
  CLASS_PATH=$(find $HOME/.m2 -name "*jar"|xargs|tr ' ' ':')
  TGT_JAR_CLASS_PATH="$(echo "$@" | tr ' ' ':')"
  echo $JAVA_CMD -cp \"$(find $(pwd) -name jardump-1.0-SNAPSHOT.jar):$CLASS_PATH:$TGT_JAR_CLASS_PATH\" app/App "$@" | bash
}
EOS
source ~/.bashrc
```

# ヘルプ
```
$jardump
Usage
jardump /home/kuraine/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar
```

# IN

  - 単一jarファイルのみ指定可能。複数jarファイルは単一を繰り返す。
```
$jardump /home/kuraine/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar
```

  - 複数jarファイルも対応できるようにalias functionを修正
```
$jardump /home/kuraine/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar /home/kuraine/.m2/repository/junit/junit/3.8.1/junit-3.8.1.jar
```

# OUT

```
行番号	CCCCC-00-クラス名	CCCCC-01-定数名	MMMMM-00-クラス名	MMMMM-01-アクセス修飾子	MMMMM-02-戻り値の型	MMMMM-03-メソッド名	MMMMM-04-可変長引数があるか	MMMMM-05-引数の個数	MMMMM-06-型パラメータリスト	MMMMM-07-型パラメータ記号リスト	MMMMM-08-引数の型リスト	MMMMM-09-仮引数の変数名リスト
00001-commons-lang-2.4.jar-00001-00001			org.apache.commons.lang.exception.NestableError	public	java.lang.String[]	getMessages	false	0				
00002-commons-lang-2.4.jar-00001-00002			org.apache.commons.lang.exception.NestableError	public	java.lang.Throwable	getCause	false	0				
00003-commons-lang-2.4.jar-00001-00003			org.apache.commons.lang.exception.NestableError	public	java.lang.String	getLocalizedMessage	false	0				
00004-commons-lang-2.4.jar-00001-00004			org.apache.commons.lang.exception.NestableError	public	boolean	equals	false	1			java.lang.Object	arg0
00005-commons-lang-2.4.jar-00001-00005			org.apache.commons.lang.exception.NestableError	public	java.lang.StackTraceElement[]	getStackTrace	false	0				
00006-commons-lang-2.4.jar-00001-00006			org.apache.commons.lang.exception.NestableError	public final synchronized	void	addSuppressed	false	1			java.lang.Throwablearg0
00007-commons-lang-2.4.jar-00001-00007			org.apache.commons.lang.exception.NestableError	public	java.lang.Throwable	getThrowable	false	1			int	arg0
00008-commons-lang-2.4.jar-00001-00008			org.apache.commons.lang.exception.NestableError	public	int	getThrowableCount	false	0				
00009-commons-lang-2.4.jar-00001-00009			org.apache.commons.lang.exception.NestableError	public final	void	printPartialStackTrace	false	1			java.io.PrintWriter	arg0
```

- MORE
  - 出力したファイルをgitにコミットするときれいに見れる
```
$jardump /home/kuraine/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar >commons-lang-2.4.jar.tsv
```
