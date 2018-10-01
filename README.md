### 待解题目
有一个猜数字游戏，庄家预先写下一个四位数字（每位数字各不相同），玩家每次随机猜一个数字，庄家告知玩家猜对了几A几B（A代表数字和位置都相同，B代表包含该数字但位置不同，比如如果庄家写的是3514，玩家猜的是3165，庄家会回答1A2B），玩家继续猜，直到猜中为止。如果超过5轮没猜中，则玩家输，否则玩家赢。请为玩家设计一个猜数字的算法，确保玩家能够大概率胜。
例如：庄家写下9876，玩家第一次猜0123，庄家回复0A0B；玩家继续猜4567，庄家恢复0A2B；依次下去，知道玩家猜中9876为止。

### 解题背景
一开始没有接触过这个游戏，所以第一时间写了一个简单的android程序来体验下规律，顺便方便后面测试算法使用。玩了一段时间发现过程大概分为确定正确数范围（哪几次猜数中确定哪些区间存在正确数），然后经过互换顺序等调整算出最后答案。

在10个不同数中确定4个数的范围，则最多需要[10-1/4]次不重复猜测。于是前期用0123作为第一次尝试，4567作为第二次尝试，可能出现以下多种情况：

1. 一次尝试正确，4A0B
2. 一次尝试数字确定但是位置不全对，xAyB（x+y = 4）
3. 1A0B + 1A0B => 剩余24组，包含8和9
4. 1A0B + 2A0B => 剩余24组，包含8或者9
5. 1A0B + 3A0B => 剩4组，不包含8和9
6. 2A0B + 0A0B => 剩12组，包含8和9
7. 2A0B + 2A0B => 剩6组，不包含8和9
8. 3A0B + 0A0B => 剩8组，包含8或者9
9. 0A1B + 0A1B => 剩216组，包含8和9
10. 0A1B + 0A2B => 剩502组，包含8或者9
11. 0A1B + 0A3B => 剩132组，不包含8和9
12. 0A2B + 0A0B => 剩84组  包含8和9
13. 0A2B + 0A2B => 剩294组 不包含8和9
14. 0A3B + 0A0B => 剩88组 包含8或者9
15. 0A1B + 2A0B => 剩72组，包含8或者9
16. 0A1B + 3A0B => 剩12组  不包含8和9
17. 0A2B + 1A0B => 剩168组 包含8或者9
18. 0A2B + 2A0B => 剩42组 不包含8和9
19. 0A3B + 1A0B => 剩44组 不包含8或者9

而过后进入第二个阶段，需要调整位置，发现不同的组别调整会出现很多不动的做法，所以尝试使用“去除不包含”项用于缩小选择空间。


#### 函数约束
* 反馈函数
假设反馈函数 K(x，y)，x为输入数据，y为校验数据（正确答案）则计算反馈 k = K(x，y)；

#### 算法一-“裁剪不可能出现的分支”
>思路为：初始化一个包含所有可能的待选答案集合。每次用户输入x，得到反馈k1。判断反馈y是否是正确答案，如果是则直接结束，如果不是则依次计算待选答案集中每一个元素于输入数据的反馈k2，如果k1不等于k2，则直接剔除。
下面为java代码实现
下面为java代码

```
   @Nullable
    public static SubmitFeedBack makeRecommendInput(int[] result, int[] input, List<int[]> data) {
		//...略去非主干
		//1. 计算两个数组的 AB 性
        Pair<Integer, Integer> inputResult = GameUtils.calResultForEachSubmit(input, result);

		// 返回成功提示
        if (inputResult != null && inputResult.first == 4) {
            return new SubmitFeedBack(true, inputResult);
        }
		
		//遍历待选集合剔除不可能出现的分支
        for (int i = 0; i < data.size(); i++) {
            int[] item = data.get(i);
			//比较两个数组的 AB 性是否一样，不一致则直接剔除。
            if (!GameUtils.submitResultEquals(inputResult, GameUtils.calResultForEachSubmit(item, input))) {
                data.remove(i);
                i--;
            }
        }
		//返回给程序反馈，其中data.get(0)是推荐用户输入
        return new SubmitFeedBack(false, inputResult, data, data.get(0),data.size());
    }
```
测试1W数据测试，有以下数据

```
0完成次数：0
1完成次数：4
2完成次数：22
3完成次数：214
4完成次数：1096
5完成次数：3384
6完成次数：3482
7完成次数：1510
8完成次数：274
9完成次数：14
```

能在5次赢得游戏概率大概 47% 左右。把数据量放大，概率越等于5成左右。我们的目的是能大概率获胜，那么上述算法还存在优化的空间？

#### 算法二-“基于反馈计算的预测实现”
> 上面代码在计算遍历待选集合剔除不可能出现的分支过程中提出了，但是剩余的集合我们只是推荐了第一个给用户输入，实际上剩余推荐池中可能存在比第一个数更优的答案。所以设计一个方法，从剩余待选集合中找出更优解。

写一个方法，在返回推荐值是返回该方法的返回值。
```
    public static int[] getSubmitInput() {
        double maxEntropy = 0;
        int[] feedBacks;
        int[] recommendSubmit = null;

        // 计算待选集合
        for (int i = 0; i < GameUtils.ALL_SUBMIT_NUM; i++) {

			//剔除已经并不可能的选项
            if (hasSubmitState[i] == 1)
                continue;
			
			//计算待选集合中每个元素对整个集合的反馈分布，区间为 
			//定义xAyB，则[0，5*x+y]
            feedBacks = EntropyUtils.makeFeedBacks();
            getAllFeedBacks(feedBacks, allSubmits.get(i));

			//针对每个区间计算最大信息值
            double entropy = calEntropy(feedBacks);

            if (entropy < 0.00001) {
                hasSubmitState[i] = 1;
                continue;
            }

            if (maxEntropy < entropy) {
                maxEntropy = entropy;
                recommendSubmit = allSubmits.get(i);
            }
        }
		//返回最大信息
        return recommendSubmit;
    }

````
引用信息熵计算，累计每种可能事件包含的信息量计算采用不确定函数，保证信息量是概率的单调递减函数，也保证了两个独立事件所产生的不确定性应该等于各自不确定性之和。

```
    public static double calEntropy(int[] feedBacks) {
        int sum = 0;
        double entropy = 0.;
        for (int i = 0; i < feedBacks.length; i++) {
            sum += feedBacks[i];
        }
		//包含所有可能事件的集合，计算熵
        for (int j = 0; j < feedBacks.length; j++) {
            //跳过 0A0B 场景
            if (feedBacks[j] == 0)
                continue;
            float tmp = (float) feedBacks[j] / sum;
            entropy +=  tmp * (-1) * log(tmp);
        }
        return entropy;
    }
```
测试1000数据测试，有以下数据

```
0完成次数：0
1完成次数：0
2完成次数：0
3完成次数：9
4完成次数：31
5完成次数：481
6完成次数：450
7完成次数：29
8完成次数：0
9完成次数：0
```
程序跑得比较慢，其中在变量计算信息熵的时候，应该还有很大的优化空间。不过方向上应该是对的。
由于时间比较紧，暂时优化到当前场景。
代码存放在githu上，欢迎讨论和意见交流。
[点击下载测试app](https://github.com/YummyLau/NumGame/blob/master/doc/app-debug.apk?raw=true)
测试运行图
<img src="https://raw.githubusercontent.com/YummyLau/NumGame/master/doc/device-2018-10-01-190051.png" width = "540" height = "960" alt="activity layout" align=center />

