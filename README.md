# SignatureView
自定义透明背景的签名View
# 预览
![效果](./ic_preview.png)
# 资源
|名字|资源|
|-|-|
|AAR|[signature_view.aar](https://github.com/RelinRan/SignatureView/blob/master/signature_view.aar)|
|Gitee|[SignatureView](https://gitee.com/relin/SignatureView)|
|GitHub |[SignatureView](https://github.com/RelinRan/SignatureView)|
# Maven
1.build.grade | setting.grade
```
repositories {
	...
	maven { url 'https://jitpack.io' }
}
```
2./app/build.grade
```
dependencies {
	implementation 'com.github.RelinRan:SignatureView:2022.6.21.1'
}
```
# xml
~~~
<com.androidx.widget.SignatureView
    android:id="@+id/signature"
    android:layout_width="match_parent"
    android:layout_height="160dp"/>
~~~
# attrs.xml
~~~
<!--画笔颜色-->
<attr name="strokeColor" format="color" />
<!--笔锋宽度-->
<attr name="strokeWidth" format="dimension" />
<!--保存的文件夹名称-->
<attr name="dir" format="string|reference" />
~~~
# 使用
注意：需要文件写入读取权限
~~~
//找到View
SignatureView signature = findViewById(R.id.signature);
//获取文件
File file = signature.getFile();
//重新绘制
signature.clear();
~~~
