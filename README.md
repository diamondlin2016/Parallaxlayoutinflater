# Parallaxlayoutinflater

![仿小红书.gif](https://github.com/diamondlin2016/Parallaxlayoutinflater/blob/master/blog/%E4%BB%BF%E5%B0%8F%E7%BA%A2%E4%B9%A6.gif)

博客链接：[搞事情，自定义 LayoutInflate 实现酷炫引导页](http://www.jianshu.com/p/b400b3547bee)



# 冒泡排序
广义上来说，是一种计算机科学领域的排序算法。
冒泡排序是一种比较简单的排序算法，在学习算法之前，我们要先想清楚，为什么需要用到算法。
说的接地气一点，我们使用算法就是为了更简单高效的解决问题。

算法是为了更高效的解决问题，评定算法主要从“时间复杂度”、“空间复杂度”两个因素来考虑。



### 时间复杂度
算法的时间复杂度是指执行算法需要的计算工作量（工作时间），一般来说，计算机算法问题规模n的函数f（n），算法的时间复杂度也记作T（n） = O（f（n））。

### 空间复杂度

算法的空间复杂度是指算法需要消耗的内存空间。

### 冒泡排序原理

冒泡排序算法的运作流程如下：

1.比较相邻的元素。如果第二个比第一个大，就交换他们两个。
2.对每一对相邻的元素作相同的工作，从开始第一对到结尾最后一对。这一遍下来之后，最后的元素就是最大的了。
3.针对所有元素重复以上步骤，找出第二大的元素放到倒数第二个元素，最后一个元素可以不用比较。
4.持续每次对剩下的元素重复上面步骤，直到没有需要比较的元素。

为了便于理解，我在网上找了一张冒泡排序图解：
[图解冒泡](!https://images2015.cnblogs.com/blog/739525/201603/739525-20160329100034660-1420925220.gif)

### 冒泡的代码实现

```Java

public class BubbleSortTest {
    public static void main(String[] args) {
        int array[] = { 6, 5, 3, 1, 8, 7, 2, 4 };    // 从小到大冒泡排序
        bubbleSort(array);
        System.out.println("冒泡排序结果：");
        System.out.println(Arrays.toString(array));
    }

    private static void bubbleSort(int[] arrays) {
        for (int i = 0; i < arrays.length - 1; i++) {
            for (int j = 0; j < arrays.length - 1 - i; j++) {
                if (arrays[j] < arrays[j + 1]) {
                    swap(arrays, j, j + 1);
                }
            }
        }
    }

    static void swap(int A[], int i, int j) {
        int temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }
}

```

### 冒泡排序的改进：不做无用功

假设有这样一个数组需要排序{9,1,2,3,4,5,6,7},正常情况下需要做7+6+5+4+3+2+1次比较才会排序介绍，但是在做完第一轮的7次排序之后，就已经排好了，后面的几轮遍历都不会发生元素交换。因此，我们可以设一个标记 flag，在一轮结束之后如果没有发生元素交换，则说明已经排序成功。后面的排序就不用进行了
代码实现如下：

```Java

    private static void bubbleSort(int[] arrays) {
        boolean flag = false;
        for (int i = 0; i < arrays.length - 1 && !flag; i++) {
            flag = false;
            for (int j = 0; j < arrays.length - 1 - i; j++) {
                if (arrays[j] < arrays[j + 1]) {
                    swap(arrays, j, j + 1);
                    flag = true;
                }
            }
        }
    }


```

### 冒泡排序的改进：记录犯罪现场

在冒泡排序的每趟扫描中，记住第一次交换发生的位置firstExchange，也能有效的提高效率。因为该位置之前的相邻记录已经有序，下一趟排序开始的时候，0-firstExchange已经是有序的了，firstExchange到n-1是无序区。所以一趟排序可能使当前有序区扩充多个记录，即较大缩小无序区范围，而非递减1，以此减少排序趟数。

代码实现如下：

```Java

    private static void bubbleSort(int[] arrays) {
        boolean flag = false;
        int firstExchange = 0;
        for (int i = 0; i < arrays.length - 1 && !flag; i++) {
            flag = false;
            for (int j = firstExchange; j < arrays.length - 1 - i; j++) {
                if (arrays[j] < arrays[j + 1]) {
                    if (!flag) {
                        firstExchange = j - 1 < 0 ? 0 : j - 1;
                    }
                    swap(arrays, j, j + 1);
                    flag = true;
                }
            }
        }
    }

```

### 冒泡排序的改进：鸡尾酒排序法


此算法与冒泡排序的不同处在于从低到高然后从高到低，而冒泡排序则仅从低到高去比较序列里的每个元素。他可以得到比冒泡排序稍微好一点的效能。

图解鸡尾酒排序：

[鸡尾酒排序](!https://images2015.cnblogs.com/blog/739525/201603/739525-20160328160227004-680964122.gif)



```Java

     static void cocktailSort(int arrays[], int n) {
        int left = 0;                            // 初始化边界
        int right = n - 1;
        boolean exchange = true;
        while (left < right && exchange) {
            exchange = false;
            for (int i = left; i < right; i++)   // 前半轮,将最大元素放到后面
            {
                if (arrays[i] > arrays[i + 1]) {
                    swap(arrays, i, i + 1);
                    exchange = true;
                }
            }
            right--;
            for (int i = right; i > left; i--)   // 后半轮,将最小元素放到前面
            {
                if (arrays[i - 1] > arrays[i]) {
                    swap(arrays, i - 1, i);
                    exchange = true;
                }
            }
            left++;
        }
    }

```
