# SimpleUtils

### 将自己的工程上传到maven仓库中并可以使用

* 发布issue地址,官网

> https://issues.sonatype.org/browse/OSSRH-42504?filter=-2

* 浏览自己发布的jar包

> https://oss.sonatype.org

* 教程

> https://www.sojson.com/blog/250.html

> https://www.cnblogs.com/binarylei/p/8628245.html

* Gpg4win生成密钥

> https://www.gpg4win.org/download.html

> 
	key: F5E88AEFE9CF263C

>
	ASCII armored output forced.
	-----BEGIN PGP PUBLIC KEY BLOCK-----
	Comment: This is a revocation certificate

	iQFMBCABCAA2FiEE8WQXA2UBQyPLw/oO9eiK7+nPJjwFAluXXIEYHQBjb252aW5p
	ZW50IHRvIGNhbmNlbAo7AAoJEPXoiu/pzyY8+SAH/241YkNtNsmuUb/PsClb2f3l
	IHnTwGgdKd4eZbf8u34DMMC2Jj68HrZd1Tq4mXrKPexSIJaUWOD+LUx+lKbgJ6M0
	YLrCFmRX///n1jb5KOuk6E27q/Z5hnz06wufwJLOTJB7ykFc+Y04lLsb9Zq6Hk2o
	+KBNp0KaDMgZ6Zrc2Zt5dJjDOcg9401/RZM5dmFSJaoWaMByQgQzi+mlTo1s16SQ
	ILwQ/oWkDVuQhD2T+farSaGA63ghKxnRXf1l2t20QjX141wymHy1Txybv8NjyXZ/
	gI0burQArZS/+OmzNioGsRQY9t2IraRbQKmOSV1DP4qejR0b0pvGfhjvmuc/gog=
	=NA62
	-----END PGP PUBLIC KEY BLOCK-----
	Revocation certificate created.

* 在maven仓库发布一个自己的jar包流程

> 1.申请issue:[官网地址](https://issues.sonatype.org/browse/)
> 2.申请的时候需要需要选择最上方的create
>
	1.project要选择Community Support - Open Source Project Repository Hosting (OSSRH)
	2.issue type:new project
	3.groupid:如果有自己的域名服务器,可以写自己的,如果是托管在git或其他服务器的,写托管服务器的
	4.scmurl:项目url地址
	5.创建完成之后等待工作人员审核,有时差,可能一天时间,碰到休息日,3天
	6.审核通过之后需要打包发布项目到maven
	7.pom文件的groupid要和申请的一样,url标签必须写正确,否则后面会报错,上传不通过
	8.pom除了dependencies之外,其余的按照自己的写,plugins和profiles可照写,version可自己改
	9.发布时候需要改写本地maven的setting.xml文件,若是用eclipse不发,需要修改eclipse的maven配置
	10.setting.xml的server中要写自己的ossr的申请的用户名和密码,可以配置2个,也可配置一个.
		<servers>
			<server>
	      <id>nexus-snapshots</id>
	      <username></username>
	      <password></password>
	    	</server>
	    	<server>
	      <id>nexus-release</id>
	      <username></username>
	      <password></password>
	    	</server>
    </servers>
    11.id要和pom文件中distributionManagement标签中的id一样,url是固定的ossr的服务
    12.pom文件中必须配置jar-source和javadoc的插件,否则报错
    13.javadoc插件最好加上-Xdoclint:none这几行,因为javadoc要生成文档,若是某些方法有注释,但是
    没有param,也没有return这些系统已经定义好的标签,打包报错.自定义的标签也报错
    14.gpg加密插件,需要在gpg的官网上下载,并且在本地生成一个公钥,之后上传到服务器,直接用图形界面操作即可
    15.最终发布命令,进入到当前文件夹:mvn clean deploy -P release -Dgpg.passphrase=密码
