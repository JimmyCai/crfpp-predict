## CRFPP-Predict
CRF++ Predict部分的Java实现

### 使用方法:
参考src/test/com.xiaomi.ad.matrix.crfpp/CRFTest.java

1. 用CRF++训练得到模型

命令: crf_learn -f 4 -c 4.0 -t model train.txt

(注意: UID必须按照ID排序，否则预测结果会和CRF++不一致)

2.调用CRFModelFacade.getFromPath(txt模型路径)，会在model路径下生成一个bin文件

这么做的原因是可以加快模型加载

3.如果想把模型文件放在Resources文件夹下，则拷贝bin文件放入Resources，调用CRFModelFacade.getFromResource(xxx)

注意文件名不需要加"/"，也不需要加".bin"，系统会自动加